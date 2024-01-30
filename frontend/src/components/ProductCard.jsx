import "./styles/product_card.styles.css";
import ImagePlaceholder from "../assets/image-placeholder.png";
import {useTranslation} from "react-i18next";
import {useContext, useEffect, useRef, useState} from "react";
import {BAD_REQUEST_STATUS_CODE, OVERLAPPING_PROMOTIONS_ERROR, PRICE_DECIMAL_DIGITS, PROMOTION_TYPE} from "../utils.js";
import {useMutation} from "@tanstack/react-query";
import {useApi} from "../hooks/useApi.js";
import {useRestaurantService} from "../hooks/services/useRestaurantService.js";
import {ErrorMessage, Field, Form, Formik} from "formik";
import {
    CreateInstantPromotionSchema,
    CreateScheduledPromotionSchema,
    EditProductSchema
} from "../data/validation.js";
import ApiContext from "../contexts/ApiContext.jsx";

function ProductCard({
    productId,
    name,
    description,
    price,
    discount,
    imageUrl,

    addProductToCart,
    disabled,

    edit = false,
    currentCategory = null,
    categories = null,
    productUrl = null,
    promotionUrl = null,
    promotionsUrl = null,
    promotionStartDate = null,
    promotionEndDate = null,
    refetch
}) {
    const DECREMENT_QUANTITY = -1;
    const INCREMENT_QUANTITY = 1;
    const MAX_QUANTITY = 100;
    const MIN_QUANTITY = 1;
    const MAX_COMMENT_LENGTH = 120;

    const { t, i18n } = useTranslation();
    const api = useApi();
    const apiContext = useContext(ApiContext);
    const restaurantService = useRestaurantService(api);

    const createPromotionFormRef = useRef();
    const editProductFormRef = useRef();

    const [quantity, setQuantity] = useState(1);
    const [comments, setComments] = useState("");
    const [showDeleteProductError, setShowDeleteProductError] = useState(false);
    const [showDeletePromotionError, setShowDeletePromotionError] = useState(false);
    const [showCreatePromotionError, setShowCreatePromotionError] = useState(false);
    const [promotionType, setPromotionType] = useState(PROMOTION_TYPE.INSTANT);
    const [showCreatePromotionOverlappingError, setShowCreatePromotionOverlappingError] = useState(false);
    const [showEditProductError, setShowEditProductError] = useState(false);

    const handleChangeQuantity = (newValue, increment) => {
        const value = parseInt(newValue);
        if (increment && quantity + increment <= MAX_QUANTITY && quantity + increment >= MIN_QUANTITY) {
            setQuantity(quantity + increment);
        } else if (!isNaN(value) && value <= MAX_QUANTITY && value >= MIN_QUANTITY) {
            setQuantity(value);
        }
    };

    const handleChangeComments = (newValue) => {
        if (newValue.length <= MAX_COMMENT_LENGTH) {
            setComments(newValue);
        }
    };

    useEffect(() => {
        if (!edit) {
            document.querySelector(`#add-${productId}-to-cart`).addEventListener("hidden.bs.modal", () => {
                setQuantity(1);
                setComments("");
            });
        } else {
            if (discount) {
                document.querySelector(`#delete-promotion-${productId}-modal`).addEventListener("hidden.bs.modal", () => {
                    setShowDeletePromotionError(false);
                });
            } else {
                document.querySelector(`#delete-product-${productId}-modal`).addEventListener("hidden.bs.modal", () => {
                    setShowDeleteProductError(false);
                });
                document.querySelector(`#create-promotion-${productId}-modal`).addEventListener("hidden.bs.modal", () => {
                    createPromotionFormRef.current?.resetForm();
                    setShowCreatePromotionError(false);
                    setShowCreatePromotionOverlappingError(false);
                    setPromotionType(PROMOTION_TYPE.INSTANT);
                });
                document.querySelector(`#edit-product-${productId}-modal`).addEventListener("hidden.bs.modal", () => {
                    editProductFormRef.current?.resetForm();
                    setShowEditProductError(false);
                });
            }
        }
    });

    const deleteProductMutation = useMutation({
        mutationFn: () => (
            restaurantService.deleteProduct(productUrl)
        ),
        onSuccess: async () => {
            // eslint-disable-next-line no-undef
            bootstrap.Modal.getOrCreateInstance(document.querySelector(`#delete-product-${productId}-modal`)).hide();
            await refetch();
        },
        onError: () => setShowDeleteProductError(true)
    });
    const deletePromotionMutation = useMutation({
        mutationFn: () => (
            restaurantService.deletePromotion(promotionUrl)
        ),
        onSuccess: async () => {
            // eslint-disable-next-line no-undef
            bootstrap.Modal.getOrCreateInstance(document.querySelector(`#delete-promotion-${productId}-modal`)).hide();
            await refetch();
        },
        onError: () => setShowDeletePromotionError(true)
    });
    const createPromotionMutation = useMutation({
        mutationFn: async ({percentage, days, minutes, hours, startDateTime, endDateTime}) => (
            await restaurantService.createPromotion(
                promotionsUrl,
                {
                    sourceProductId: productId,
                    percentage: percentage,
                    type: promotionType,
                    ...(promotionType === PROMOTION_TYPE.INSTANT && {days: days, hours: hours, minutes: minutes}),
                    ...(promotionType === PROMOTION_TYPE.SCHEDULED && {startDateTime: startDateTime, endDateTime: endDateTime})
                }
            )
        )
    });
    const editProductMutation = useMutation({
        mutationFn: async ({productName, description, price, category, image}) => (
            await restaurantService.editProduct(
                productUrl,
                apiContext.imagesUrl,
                productName,
                description,
                price,
                category,
                image
            )
        )
    });

    const handleCreatePromotion = (values, {setSubmitting}) => {
        createPromotionMutation.mutate(
            {
                percentage: values.percentage,
                days: values.days,
                hours: values.hours,
                minutes: values.minutes,
                startDateTime: values.startDateTime,
                endDateTime: values.endDateTime
            },
            {
                onSuccess: async () => {
                    // eslint-disable-next-line no-undef
                    bootstrap.Modal.getOrCreateInstance(document.querySelector(`#create-promotion-${productId}-modal`)).hide();
                    await refetch();
                },
                onError: (error) => {
                    if (error.response.status === BAD_REQUEST_STATUS_CODE && error.response.data.some(err => err.path === OVERLAPPING_PROMOTIONS_ERROR)) {
                        setShowCreatePromotionOverlappingError(true);
                    } else {
                        setShowCreatePromotionError(true);
                    }
                }
            }
        );
        setSubmitting(false);
    };

    const handleEditProduct = (values, {setSubmitting}) => {
        const image = values.image !== "" ? values.image : null;
        const category = values.category !== currentCategory ? values.category : null;
        editProductMutation.mutate(
            {
                productName: values.productName,
                description: values.description,
                price: values.price,
                category: category,
                image: image
            },
            {
                onSuccess: async (categoryId) => {
                    // eslint-disable-next-line no-undef
                    bootstrap.Modal.getOrCreateInstance(document.querySelector(`#edit-product-${productId}-modal`)).hide();
                    await refetch(categoryId);
                },
                onError: () => setShowEditProductError(true)
            }
        );
        setSubmitting(false);
    };

    const card = (
        <div className={"card" + (edit && discount && promotionStartDate > Date.now() ? " promotion-disabled" : "")}>
            <div className="image-container">
                <img src={imageUrl || ImagePlaceholder} alt={name} className="img-fluid rounded-start"/>
            </div>
            <div className="d-flex flex-column space-between w-100">
                <div className="card-body">
                    <div>
                        {
                            edit
                                ?
                                <div className="d-flex justify-content-between">
                                    <p className="card-text">{name}</p>
                                    <div className="d-flex gap-2 ps-2">
                                        {
                                            discount
                                                ?
                                                <i
                                                    className="bi bi-x-circle text-danger default clickable-object"
                                                    data-bs-toggle="modal"
                                                    data-bs-target={`#delete-promotion-${productId}-modal`}
                                                >
                                                </i>
                                                :
                                                <>
                                                    <i
                                                        className="bi bi-percent default text-promotion clickable-object"
                                                        data-bs-toggle="modal"
                                                        data-bs-target={`#create-promotion-${productId}-modal`}
                                                    >
                                                    </i>
                                                    <i
                                                        className="bi bi-pencil-fill clickable-object"
                                                        data-bs-toggle="modal"
                                                        data-bs-target={`#edit-product-${productId}-modal`}
                                                    >
                                                    </i>
                                                    <i
                                                        className="bi bi-trash-fill default text-danger clickable-object"
                                                        data-bs-toggle="modal"
                                                        data-bs-target={`#delete-product-${productId}-modal`}
                                                    >
                                                    </i>
                                                </>
                                        }
                                    </div>
                                </div>
                                :
                                <p className="card-text">{name}</p>
                        }
                        <p className="card-text">
                            <small className="text-body-secondary">{description || t("restaurant.product.no_description")}</small>
                        </p>
                    </div>
                    <h5 className="card-title">
                        ${price}
                        {discount && <span className="badge bg-promotion ms-2">-{discount}%</span>}
                    </h5>
                </div>
                {
                    discount && edit &&
                    <div className="card-footer">
                        <small>
                            <span className="text-promotion">{t("restaurant.edit.promotion_from")}</span> {promotionStartDate.toLocaleString(i18n.language)}
                        </small>
                        <br/>
                        <small>
                            <span className="text-promotion">{t("restaurant.edit.promotion_to")}</span> {promotionEndDate.toLocaleString(i18n.language)}
                        </small>
                    </div>
                }
            </div>
        </div>
    );

    return (
        <>
            <div className="product_card">
                {
                    !edit
                        ?
                        <div className="clickable-object" data-bs-toggle="modal" data-bs-target={`#add-${productId}-to-cart`}>
                            {card}
                        </div>
                        :
                        <div>
                            {card}
                        </div>
                }
            </div>

            {
                !edit &&
                <div className="modal fade add-product-to-cart" id={`add-${productId}-to-cart`} tabIndex="-1">
                    <div className="modal-dialog modal-dialog-centered">
                        <div className="modal-content">
                            <div className="modal-header" style={{"--image": `url(${imageUrl || ImagePlaceholder})`}}>
                                <button type="button" className="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <div className="modal-body">
                                <h4 id="add-item-to-cart-title">{name}</h4>
                                <p id="add-item-to-cart-description">{description || t("restaurant.product.no_description")}</p>
                                <hr/>
                                <div className="inputs">
                                    <div className="input-group">
                                        <button className="btn btn-secondary" type="button" onClick={() => handleChangeQuantity(undefined, DECREMENT_QUANTITY)}>
                                            <i className="bi bi-dash default"></i>
                                        </button>
                                        <input type="number" className="form-control" value={quantity} onChange={(event) => handleChangeQuantity(event.target.value)}/>
                                        <button className="btn btn-secondary" type="button" onClick={() => handleChangeQuantity(undefined, INCREMENT_QUANTITY)}>
                                            <i className="bi bi-plus default"></i>
                                        </button>
                                    </div>
                                    <input placeholder={t("restaurant.product.comments")} className="form-control" value={comments} onChange={(event) => handleChangeComments(event.target.value)}/>
                                </div>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-primary" data-bs-dismiss="modal" onClick={() => addProductToCart(productId, name, price, quantity, comments)} disabled={disabled}>
                                    {t("restaurant.product.add_to_cart", {price: (price * quantity).toFixed(PRICE_DECIMAL_DIGITS)})}
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            }

            {
                edit && discount &&
                <div className="modal fade" id={`delete-promotion-${productId}-modal`} tabIndex="-1">
                    <div className="modal-dialog modal-dialog-centered">
                        <div className="modal-content">
                            <div className="modal-body">
                                {showDeletePromotionError &&
                                    <div className="alert alert-danger" role="alert">{t("restaurant.edit.error")}</div>}
                                <h1 className="modal-title fs-5">{t("restaurant.edit.delete_promotion")}</h1>
                            </div>
                            <div className="modal-footer">
                                <button type="button" data-bs-dismiss="modal" className="btn btn-secondary">{t("paging.no")}</button>
                                <button type="button" className="btn btn-danger" onClick={deletePromotionMutation.mutate}>{t("paging.yes")}</button>
                            </div>
                        </div>
                    </div>
                </div>
            }

            {
                edit && !discount &&
                <>
                    <div className="modal fade" id={`delete-product-${productId}-modal`} tabIndex="-1">
                        <div className="modal-dialog modal-dialog-centered">
                            <div className="modal-content">
                                <div className="modal-body">
                                    {showDeleteProductError &&
                                        <div className="alert alert-danger" role="alert">{t("restaurant.edit.error")}</div>}
                                    <h1 className="modal-title fs-5">{t("restaurant.edit.delete_product")}</h1>
                                </div>
                                <div className="modal-footer">
                                    <button type="button" data-bs-dismiss="modal" className="btn btn-secondary">{t("paging.no")}</button>
                                    <button type="button" className="btn btn-danger" onClick={deleteProductMutation.mutate}>{t("paging.yes")}</button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div className="modal fade" id={`create-promotion-${productId}-modal`} tabIndex="-1">
                        <div className="modal-dialog modal-dialog-centered">
                            <div className="modal-content">
                                <div className="modal-header">
                                    <h1 className="modal-title fs-5">{t("restaurant.edit.create_promotion")}</h1>
                                    <button type="button" className="btn-close" data-bs-dismiss="modal"></button>
                                </div>
                                <Formik
                                    innerRef={createPromotionFormRef}
                                    initialValues={{
                                        percentage: "",
                                        days: 0,
                                        hours: 0,
                                        minutes: 0,
                                        startDateTime: "",
                                        endDateTime: ""
                                    }}
                                    validationSchema={promotionType === PROMOTION_TYPE.INSTANT ? CreateInstantPromotionSchema : CreateScheduledPromotionSchema}
                                    onSubmit={handleCreatePromotion}
                                >
                                    {({isSubmitting, setErrors}) => (
                                        <Form>
                                            <div className="modal-body">
                                                {showCreatePromotionError &&
                                                    <div className="alert alert-danger" role="alert">{t("restaurant.edit.error")}</div>}
                                                <div className="mb-3">
                                                    <label htmlFor="percentage" className="form-label">{t("restaurant.edit.promotion_percentage")}</label>
                                                    <div className="input-group">
                                                        <Field name="percentage" type="number" min="1" max="99" className="form-control" id="percentage"/>
                                                        <span className="input-group-text">%</span>
                                                    </div>
                                                    <ErrorMessage name="percentage" component="div" className="form-error"/>
                                                </div>
                                                <div className="nav nav-pills nav-justified mb-3 promotion-nav" role="tablist">
                                                    <button
                                                        className="nav-link active"
                                                        data-bs-toggle="tab"
                                                        data-bs-target={`#promotion-${productId}-instant`}
                                                        type="button"
                                                        role="tab"
                                                        onClick={() => {
                                                            setPromotionType(PROMOTION_TYPE.INSTANT);
                                                            setErrors({});
                                                        }}
                                                    >
                                                        {t("restaurant.edit.instant_promotion")}
                                                    </button>
                                                    <button
                                                        className="nav-link"
                                                        data-bs-toggle="tab"
                                                        data-bs-target={`#promotion-${productId}-scheduled`}
                                                        type="button"
                                                        role="tab"
                                                        onClick={() => {
                                                            setPromotionType(PROMOTION_TYPE.SCHEDULED);
                                                            setErrors({});
                                                        }}
                                                    >
                                                        {t("restaurant.edit.scheduled_promotion")}
                                                    </button>
                                                </div>
                                                <div className="tab-content">
                                                    <div className="tab-pane active show fade" id={`promotion-${productId}-instant`} role="tabpanel" tabIndex="0">
                                                        <div className="mb-3">
                                                            <label className="form-label">{t("restaurant.edit.promotion_duration.label")}</label>
                                                            <div className="d-flex gap-3">
                                                                <div className="input-group">
                                                                    <Field name="days" type="number" min="0" className="form-control"/>
                                                                    <span className="input-group-text">{t("restaurant.edit.promotion_duration.days")}</span>
                                                                </div>
                                                                <div className="input-group">
                                                                    <Field name="hours" type="number" min="0" className="form-control"/>
                                                                    <span className="input-group-text">{t("restaurant.edit.promotion_duration.hours")}</span>
                                                                </div>
                                                                <div className="input-group">
                                                                    <Field name="minutes" type="number" min="0" className="form-control"/>
                                                                    <span className="input-group-text">{t("restaurant.edit.promotion_duration.minutes")}</span>
                                                                </div>
                                                            </div>
                                                            <ErrorMessage name="days" component="div" className="form-error"/>
                                                            <ErrorMessage name="hours" component="div" className="form-error"/>
                                                            <ErrorMessage name="minutes" component="div" className="form-error"/>
                                                            {showCreatePromotionOverlappingError &&
                                                                <div className="form-error">{t("restaurant.edit.promotion_overlapping")}</div>}
                                                        </div>
                                                    </div>
                                                    <div className="tab-pane fade" id={`promotion-${productId}-scheduled`} role="tabpanel" tabIndex="0">
                                                        <div className="mb-3">
                                                            <label htmlFor="startDateTime" className="form-label">{t("restaurant.edit.promotion_start")}</label>
                                                            <Field name="startDateTime" type="datetime-local" className="form-control" id="startDateTime"/>
                                                            <ErrorMessage name="startDateTime" component="div" className="form-error"/>
                                                        </div>
                                                        <div className="mb-3">
                                                            <label htmlFor="endDateTime" className="form-label">{t("restaurant.edit.promotion_end")}</label>
                                                            <Field name="endDateTime" type="datetime-local" className="form-control" id="endDateTime" onBlur={() => setShowCreatePromotionOverlappingError(false)}/>
                                                            <ErrorMessage name="endDateTime" component="div" className="form-error"/>
                                                            {showCreatePromotionOverlappingError &&
                                                                <div className="form-error">{t("restaurant.edit.promotion_overlapping")}</div>}
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div className="modal-footer">
                                                <button type="submit" className="btn btn-promotion" disabled={isSubmitting}>{t("restaurant.edit.create_promotion")}</button>
                                            </div>
                                        </Form>
                                    )}
                                </Formik>
                            </div>
                        </div>
                    </div>

                    <div className="modal fade" id={`edit-product-${productId}-modal`} tabIndex="-1">
                        <div className="modal-dialog modal-dialog-centered">
                            <div className="modal-content">
                                <div className="modal-header">
                                    <h1 className="modal-title fs-5">{t("restaurant.edit.edit_product")}</h1>
                                    <button type="button" className="btn-close" data-bs-dismiss="modal"></button>
                                </div>
                                <Formik
                                    innerRef={editProductFormRef}
                                    initialValues={{
                                        productName: name,
                                        description: description,
                                        price: price,
                                        category: currentCategory,
                                        image: ""
                                    }}
                                    validationSchema={() => EditProductSchema(categories)}
                                    onSubmit={handleEditProduct}
                                >
                                    {({values, isSubmitting}) => (
                                        <Form>
                                            <div className="modal-body">
                                                {showEditProductError &&
                                                    <div className="alert alert-danger" role="alert">{t("restaurant.edit.error")}</div>}
                                                <div>
                                                    <div className="mb-3">
                                                        <label htmlFor="productName" className="form-label">{t("restaurant.edit.product_name")}</label>
                                                        <Field name="productName" type="text" className="form-control" id="productName"/>
                                                        <ErrorMessage name="productName" component="div" className="form-error"/>
                                                    </div>
                                                    <div className="mb-3">
                                                        <label htmlFor="description" className="form-label">{t("restaurant.edit.product_description")}</label>
                                                        <Field as="textarea" className="form-control" name="description" id="description" rows="3"/>
                                                        <ErrorMessage name="description" component="div" className="form-error"/>
                                                    </div>
                                                    <div className="mb-3">
                                                        <label htmlFor="price" className="form-label">{t("restaurant.edit.product_price")}</label>
                                                        <div className="input-group">
                                                            <span className="input-group-text">$</span>
                                                            <Field name="price" step="0.01" min="0" type="number" className="form-control" id="price"/>
                                                        </div>
                                                        <ErrorMessage name="price" component="div" className="form-error"/>
                                                    </div>
                                                    <div className="mb-3">
                                                        <label htmlFor="category" className="form-label">{t("restaurant.edit.change_product_category")}</label>
                                                        <Field name="category" as="select" className="form-select" id="category">
                                                            {
                                                                categories.map((category) => (
                                                                    <option key={category.id} value={category.id}>{category.name}</option>
                                                                ))
                                                            }
                                                        </Field>
                                                        <ErrorMessage name="category" component="div" className="form-error"/>
                                                    </div>
                                                    <div className="mb-3">
                                                        <label htmlFor="image" className="form-label">{t("restaurant.edit.product_image")}</label>
                                                        <Field name="image">
                                                            {({field: {value, onChange, ...field}, form}) => (
                                                                <input
                                                                    type="file"
                                                                    className="form-control"
                                                                    id="image"
                                                                    accept="image/*"
                                                                    {...field}
                                                                    onChange={event => form.setFieldValue(field.name, event.currentTarget.files[0])}
                                                                />
                                                            )}
                                                        </Field>
                                                        <ErrorMessage name="image" component="div" className="form-error"/>
                                                    </div>
                                                </div>
                                                <hr/>
                                                <div className="d-flex justify-content-center product_card">
                                                    <div className="card">
                                                        <div className="image-container">
                                                            <img src={values.image ? URL.createObjectURL(values.image) : imageUrl || ImagePlaceholder} alt={values.productName || t("restaurant.edit.product_name")} className="img-fluid rounded-start"/>
                                                        </div>
                                                        <div className="card-body">
                                                            <div>
                                                                <p className="card-text">{values.productName || t("restaurant.edit.product_name")}</p>
                                                                <p className="card-text">
                                                                    <small className="text-body-secondary">{values.description || t("restaurant.edit.product_description")}</small>
                                                                </p>
                                                            </div>
                                                            <h5 className="card-title">
                                                                ${values.price || "1.00"}
                                                            </h5>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div className="modal-footer">
                                                <button type="submit" className="btn btn-primary" disabled={isSubmitting}>{t("restaurant.edit.edit_product")}</button>
                                            </div>
                                        </Form>
                                    )}
                                </Formik>
                            </div>
                        </div>
                    </div>
                </>
            }
        </>
    );
}

export default ProductCard;
