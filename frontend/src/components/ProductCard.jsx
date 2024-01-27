import "./styles/product_card.styles.css";
import ImagePlaceholder from "../assets/image-placeholder.png";
import {useTranslation} from "react-i18next";
import {useEffect, useState} from "react";
import {PRICE_DECIMAL_DIGITS} from "../utils.js";
import {useMutation} from "@tanstack/react-query";
import {useApi} from "../hooks/useApi.js";
import {useRestaurantService} from "../hooks/services/useRestaurantService.js";

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
    productUrl = null,
    promotionUrl = null,
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
    const restaurantService = useRestaurantService(api);

    const [quantity, setQuantity] = useState(1);
    const [comments, setComments] = useState("");
    const [showDeleteProductError, setShowDeleteProductError] = useState(false);
    const [showDeletePromotionError, setShowDeletePromotionError] = useState(false);

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
            document.querySelector(`#delete-product-${productId}-modal`).addEventListener("hidden.bs.modal", () => {
                setShowDeleteProductError(false);
            });
            document.querySelector(`#delete-promotion-${productId}-modal`).addEventListener("hidden.bs.modal", () => {
                setShowDeletePromotionError(false);
            });
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

    const card = (
        <div className="card">
            <div className="image-container">
                <img src={imageUrl || ImagePlaceholder} alt={name} className="img-fluid rounded-start"/>
            </div>
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
                                                >
                                                </i>
                                                <i
                                                    className="bi bi-pencil-fill clickable-object"
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
                {
                    discount &&
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
                edit &&
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
                </>
            }
        </>
    );
}

export default ProductCard;
