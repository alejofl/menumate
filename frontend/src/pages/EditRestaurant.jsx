import React, {useEffect, useRef, useState} from "react";
import Page from "../components/Page.jsx";
import {useMutation, useQuery} from "@tanstack/react-query";
import {useTranslation} from "react-i18next";
import {useApi} from "../hooks/useApi.js";
import {useContext} from "react";
import ApiContext from "../contexts/ApiContext.jsx";
import {useRestaurantService} from "../hooks/services/useRestaurantService.js";
import Error from "./Error.jsx";
import ContentLoader from "react-content-loader";
import "./styles/restaurant.styles.css";
import TagsContainer from "../components/TagsContainer.jsx";
import ProductCard from "../components/ProductCard.jsx";
import AuthContext from "../contexts/AuthContext.jsx";
import {Link, useNavigate} from "react-router-dom";
import {ErrorMessage, Field, Form, Formik} from "formik";
import {
    AddCategorySchema,
    AddEmployeeSchema,
    AddProductSchema,
    CreateRestaurantSchema,
    EditEmployeeRoleSchema
} from "../data/validation.js";
import ImagePlaceholder from "../assets/image-placeholder.png";
import Select from "react-select";
import {selectComponents, selectStyles} from "../components/utils/SelectProperties.js";
import RestaurantSpecialties from "../data/RestaurantSpecialties.js";
import RestaurantTags from "../data/RestaurantTags.js";
import {ROLE_FOR_RESTAURANT} from "../utils.js";

// eslint-disable-next-line no-unused-vars
function EditRestaurant({restaurant, categories, products, promotions, promotionProducts, userRole, refetchRestaurant, refetchCategories, refetchPromotions}) {
    const {t} = useTranslation();
    const navigate = useNavigate();
    const api = useApi();
    const apiContext = useContext(ApiContext);
    const authContext = useContext(AuthContext);
    const restaurantService = useRestaurantService(api);

    const {
        isPending: employeesIsPending,
        isError: employeesIsError,
        data: employees,
        error: employeesError,
        refetch: refetchEmployees
    } = useQuery({
        queryKey: ["restaurant", restaurant.restaurantId, "employees"],
        queryFn: async () => (
            await restaurantService.getEmployees(restaurant.employeesUriTemplate.fill({}))
        )
    });

    const specialties = RestaurantSpecialties.map(specialty => ({
        label: t(`restaurant_specialties.${specialty}`),
        value: specialty
    }));
    const tags = RestaurantTags.map(tag => ({
        label: t(`restaurant_tags.${tag}`),
        value: tag
    }));

    const addCategoryFormRef = useRef();
    const addProductFormRef = useRef();
    const editRestaurantInformationFormRef = useRef();
    const editCategoryFormRef = useRef();
    const addEmployeeFormRef = useRef();
    const editEmployeeRoleFormRef = useRef();

    const [showDeleteRestaurantError, setShowDeleteRestaurantError] = useState(false);
    const [showAddCategoryError, setShowAddCategoryError] = useState(false);
    const [showAddProductError, setShowAddProductError] = useState(false);
    const [addProductUrl, setAddProductUrl] = useState("");
    const [showEditRestaurantInformationError, setShowEditRestaurantInformationError] = useState(false);
    const [showEditCategoryError, setShowEditCategoryError] = useState(false);
    const [editCategoryName, setEditCategoryName] = useState("");
    const [editCategoryUrl, setEditCategoryUrl] = useState("");
    const [showDeleteCategoryError, setShowDeleteCategoryError] = useState(false);
    const [deleteCategoryUrl, setDeleteCategoryUrl] = useState("");
    const [showAddEmployeeError, setShowAddEmployeeError] = useState(false);
    const [showDeleteEmployeeError, setShowDeleteEmployeeError] = useState(false);
    const [deleteEmployeeUrl, setDeleteEmployeeUrl] = useState("");
    const [showEditEmployeeRoleError, setShowEditEmployeeRoleError] = useState(false);
    const [editEmployeeRoleUrl, setEditEmployeeRoleUrl] = useState("");

    useEffect(() => {
        if (employeesIsPending) {
            return;
        }

        document.querySelector("#add-category-modal").addEventListener("hidden.bs.modal", () => {
            addCategoryFormRef.current?.resetForm();
            setShowAddCategoryError(false);
        });
        document.querySelector("#add-product-modal").addEventListener("hidden.bs.modal", () => {
            addProductFormRef.current?.resetForm();
            setShowAddProductError(false);
            setAddProductUrl("");
        });
        document.querySelector("#edit-information-modal").addEventListener("hidden.bs.modal", () => {
            editRestaurantInformationFormRef.current?.resetForm();
            setShowEditRestaurantInformationError(false);
        });
        document.querySelector("#edit-category-modal").addEventListener("hidden.bs.modal", () => {
            editCategoryFormRef.current?.resetForm();
            setShowEditCategoryError(false);
            setEditCategoryName("");
            setEditCategoryUrl("");
        });
        document.querySelector("#delete-category-modal").addEventListener("hidden.bs.modal", () => {
            setShowDeleteRestaurantError(false);
            setDeleteCategoryUrl("");
        });
        document.querySelector("#edit-employees-modal").addEventListener("hidden.bs.modal", () => {
            addEmployeeFormRef.current?.resetForm();
            setShowAddEmployeeError(false);
            editEmployeeRoleFormRef.current?.resetForm();
            setShowEditEmployeeRoleError(false);
            setEditEmployeeRoleUrl("");
        });
        document.querySelector("#delete-employee-modal").addEventListener("hidden.bs.modal", () => {
            setShowDeleteEmployeeError(false);
            setDeleteEmployeeUrl("");
        });
    }, [employeesIsPending]);

    const deleteRestaurantMutation = useMutation({
        mutationFn: async () => {
            await restaurantService.deleteRestaurant(restaurant.selfUrl);
        },
        onSuccess: () => {
            // eslint-disable-next-line no-undef
            bootstrap.Modal.getOrCreateInstance(document.querySelector("#delete-restaurant-modal")).hide();
            navigate("/");
        },
        onError: () => setShowDeleteRestaurantError(true)
    });
    const addCategoryMutation = useMutation({
        mutationFn: async ({name}) => {
            await restaurantService.addCategory(restaurant.categoriesUrl, name);
        }
    });
    const addProductMutation = useMutation({
        mutationFn: async ({productName, description, price, image}) => {
            await restaurantService.addProduct(
                addProductUrl,
                apiContext.imagesUrl,
                productName,
                description,
                price,
                image
            );
        }
    });
    const editRestaurantInformationMutation = useMutation({
        mutationFn: async ({name, address, specialty, tags, description, maxTables, logo, portrait1, portrait2}) => {
            await restaurantService.editRestaurantInformation(
                restaurant.selfUrl,
                apiContext.imagesUrl,
                name,
                address,
                specialty,
                tags,
                description,
                maxTables,
                logo,
                portrait1,
                portrait2
            );
        }
    });
    const editCategoryMutation = useMutation({
        mutationFn: async ({name}) => {
            await restaurantService.editCategory(editCategoryUrl, name);
        }
    });
    const deleteCategoryMutation = useMutation({
        mutationFn: async () => {
            await restaurantService.deleteCategory(deleteCategoryUrl);
        },
        onSuccess: () => {
            refetchCategories();
            // eslint-disable-next-line no-undef
            bootstrap.Modal.getOrCreateInstance(document.querySelector("#delete-category-modal")).hide();
        },
        onError: () => setShowDeleteCategoryError(true)
    });
    const addEmployeeMutation = useMutation({
        mutationFn: async ({email, role}) => {
            await restaurantService.addEmployee(restaurant.employeesUriTemplate.fill({}), email, role);
        }
    });
    const deleteEmployeeMutation = useMutation({
        mutationFn: async () => {
            await restaurantService.deleteEmployee(deleteEmployeeUrl);
        },
        onSuccess: () => {
            refetchEmployees();
            // eslint-disable-next-line no-undef
            bootstrap.Modal.getOrCreateInstance(document.querySelector("#delete-employee-modal")).hide();
            // eslint-disable-next-line no-undef
            bootstrap.Modal.getOrCreateInstance(document.querySelector("#edit-employees-modal")).show();
        },
        onError: () => setShowDeleteEmployeeError(true)
    });
    const editEmployeeRoleMutation = useMutation({
        mutationFn: async ({role}) => {
            await restaurantService.editEmployeeRole(editEmployeeRoleUrl, role);
        }
    });
    const updateCategoryOrderMutation = useMutation({
        mutationFn: async ({categoryUrl, categoryOrder}) => {
            return await restaurantService.updateCategoryOrder(categoryUrl, categoryOrder);
        },
        onSuccess: async () => {
            await refetchCategories();
            for (const product of products) {
                await product.refetch();
            }
        }
    });

    const handleAddCategory = (values, {setSubmitting}) => {
        addCategoryMutation.mutate(
            {
                name: values.name
            },
            {
                onSuccess: () => {
                    refetchCategories();
                    // eslint-disable-next-line no-undef
                    bootstrap.Modal.getOrCreateInstance(document.querySelector("#add-category-modal")).hide();
                },
                onError: () => setShowAddCategoryError(true)
            }
        );
        setSubmitting(false);
    };

    const handleAddProduct = (values, {setSubmitting}) => {
        const image = values.image !== "" ? values.image : null;
        addProductMutation.mutate(
            {
                productName: values.productName,
                description: values.description,
                price: values.price,
                image: image
            },
            {
                onSuccess: () => {
                    const productIndex = categories.sort((a, b) => a.orderNum < b.orderNum).findIndex(category => category.productsUrl === addProductUrl);
                    products[productIndex].refetch();
                    // eslint-disable-next-line no-undef
                    bootstrap.Modal.getOrCreateInstance(document.querySelector("#add-product-modal")).hide();
                },
                onError: () => setShowAddProductError(true)
            }
        );
        setSubmitting(false);
    };

    const handleEditRestaurantInformation = (values, {setSubmitting}) => {
        const logo = values.logo !== "" ? values.logo : null;
        const portrait1 = values.portrait1 !== "" ? values.portrait1 : null;
        const portrait2 = values.portrait2 !== "" ? values.portrait2 : null;
        editRestaurantInformationMutation.mutate(
            {
                name: values.name,
                address: values.address,
                specialty: values.specialty,
                tags: values.tags,
                description: values.description,
                maxTables: values.maxTables,
                logo: logo,
                portrait1: portrait1,
                portrait2: portrait2
            },
            {
                onSuccess: () => {
                    refetchRestaurant();
                    // eslint-disable-next-line no-undef
                    bootstrap.Modal.getOrCreateInstance(document.querySelector("#edit-information-modal")).hide();
                },
                onError: () => setShowEditRestaurantInformationError(true)
            }
        );
        setSubmitting(false);
    };

    const handleEditCategory = (values, {setSubmitting}) => {
        editCategoryMutation.mutate(
            {
                name: values.name
            },
            {
                onSuccess: () => {
                    refetchCategories();
                    // eslint-disable-next-line no-undef
                    bootstrap.Modal.getOrCreateInstance(document.querySelector("#edit-category-modal")).hide();
                },
                onError: () => setShowEditCategoryError(true)
            }
        );
        setSubmitting(false);
    };

    const handleAddEmployee = (values, {setSubmitting, resetForm}) => {
        addEmployeeMutation.mutate(
            {
                email: values.email,
                role: values.role
            },
            {
                onSuccess: () => {
                    resetForm();
                    refetchEmployees();
                    setShowAddEmployeeError(false);
                },
                onError: () => setShowAddEmployeeError(true)
            }
        );
        setSubmitting(false);
    };

    const handleEditEmployeeRole = (values, {setSubmitting, resetForm}) => {
        editEmployeeRoleMutation.mutate(
            {
                role: values.role
            },
            {
                onSuccess: () => {
                    resetForm();
                    refetchEmployees();
                    setShowEditEmployeeRoleError(false);
                    setEditEmployeeRoleUrl("");
                },
                onError: () => setShowEditEmployeeRoleError(true)
            }
        );
        setSubmitting(false);
    };

    if (employeesIsError) {
        return (
            <Error errorNumber={employeesError.response.status}/>
        );
    } else if (employeesIsPending) {
        return (
            <>
                <Page title={t("titles.loading")}>
                    <ContentLoader backgroundColor="#eaeaea" foregroundColor="#e0e0e0" width="1920">
                        <rect x="288" y="50" rx="0" ry="0" width="1344" height="200"/>
                        <rect x="12" y="300" rx="0" ry="0" width="200" height="380"/>
                        <rect x="236" y="300" rx="0" ry="0" width="1268" height="64"/>
                        <rect x="236" y="388" rx="0" ry="0" width="400" height="200"/>
                        <rect x="656" y="388" rx="0" ry="0" width="400" height="200"/>
                        <rect x="1528" y="300" rx="0" ry="0" width="380" height="120"/>
                    </ContentLoader>
                </Page>
            </>
        );
    }
    return (
        <>
            <Page title={t("titles.edit_restaurant", {name: restaurant.name})} className="restaurant">
                <div className="header">
                    <img src={restaurant.portrait1Url} alt={restaurant.name}/>
                </div>
                <div className="d-flex justify-content-center">
                    <div className="information">
                        <img src={restaurant.logoUrl} alt={restaurant.name} className="logo"/>
                        <div className="flex-grow-1">
                            {
                                !restaurant.active &&
                                <div className="alert alert-warning" role="alert">
                                    <b>{t("restaurant.disabled")}</b> {t("restaurant.disabled_employee")}
                                </div>
                            }
                            <h1>{restaurant.name}</h1>
                            <p className="mb-1">
                                {restaurant.description || <i>{t("restaurant.no_description")}</i>}
                            </p>
                            <p className="mb-2"><i className="bi bi-geo-alt"></i> {restaurant.address}</p>
                            <TagsContainer tags={restaurant.tags} clickable={false}/>
                        </div>
                        <div className="d-flex flex-column gap-2">
                            <Link to={`/restaurants/${restaurant.restaurantId}`} className="btn btn-primary" type="button">{t("restaurant.edit.done")}</Link>
                            <button className="btn btn-secondary" type="button" data-bs-toggle="modal" data-bs-target="#edit-information-modal">{t("restaurant.edit.edit_information")}</button>
                            {authContext.isAuthenticated && (userRole.isOwner) &&
                                <>
                                    <button className="btn btn-secondary" type="button" data-bs-toggle="modal" data-bs-target="#edit-employees-modal">{t("restaurant.edit.edit_employees")}</button>
                                    <button className="btn btn-danger" type="button" data-bs-toggle="modal" data-bs-target="#delete-restaurant-modal">{t("restaurant.delete_restaurant")}</button>
                                </>
                            }
                        </div>
                    </div>
                </div>
                <div className="content">
                    <div className="categories sticky">
                        <div className="card">
                            <div className="card-header text-muted">{t("restaurant.categories")}</div>
                            <div className="card-body">
                                <div className="nav nav-pills small">
                                    {
                                        categories.sort((a, b) => a.orderNum < b.orderNum).map(category => (
                                            <button
                                                className="category-item nav-link"
                                                key={category.orderNum}
                                                onClick={(event) => {
                                                    document.querySelector(`#category-${category.orderNum}`).scrollIntoView({block: "start"});
                                                    document.querySelector(".category-item.active")?.classList.remove("active");
                                                    event.currentTarget.classList.add("active");
                                                }}
                                            >
                                                {category.name}
                                            </button>
                                        ))
                                    }
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="menu px-4">
                        {promotions.length > 0 &&
                            <React.Fragment>
                                <div className="card mb-4 bg-promotion">
                                    <div className="card-body d-flex justify-content-between align-items-center">
                                        <h3 className="mb-0 text-white">{t("restaurant.promotions")}</h3>
                                    </div>
                                </div>
                                <div className="product-container">
                                    {
                                        promotions.map((promotion, i) => (
                                            <ProductCard
                                                key={i}
                                                productId={promotionProducts[i].data.productId}
                                                name={promotionProducts[i].data.name}
                                                description={promotionProducts[i].data.description}
                                                price={promotionProducts[i].data.price}
                                                imageUrl={promotionProducts[i].data.imageUrl}
                                                discount={promotion.discountPercentage}
                                                edit={true}
                                            />
                                        ))
                                    }
                                </div>
                            </React.Fragment>
                        }
                        {
                            categories.sort((a, b) => a.orderNum < b.orderNum).map((category, i) => (
                                <React.Fragment key={category.orderNum}>
                                    <div className="card mb-4" id={`category-${category.orderNum}`}>
                                        <div className="card-body d-flex justify-content-between align-items-center">
                                            <h3 className="mb-0">{category.name}</h3>
                                            <div className="d-flex align-items-center gap-3">
                                                {
                                                    i !== 0 &&
                                                    <i
                                                        className="bi bi-arrow-up-circle-fill default text-secondary clickable-object"
                                                        onClick={() => updateCategoryOrderMutation.mutate({
                                                            categoryUrl: category.selfUrl,
                                                            categoryOrder: category.orderNum - 1
                                                        })}
                                                    >
                                                    </i>
                                                }
                                                {
                                                    i !== (categories.length - 1) &&
                                                    <i
                                                        className="bi bi-arrow-down-circle-fill default text-secondary clickable-object"
                                                        onClick={() => updateCategoryOrderMutation.mutate({
                                                            categoryUrl: category.selfUrl,
                                                            categoryOrder: category.orderNum + 1
                                                        })}
                                                    >
                                                    </i>
                                                }
                                                <i
                                                    className="bi bi-pencil-fill clickable-object"
                                                    data-bs-toggle="modal"
                                                    data-bs-target="#edit-category-modal"
                                                    onClick={() => {
                                                        setEditCategoryName(category.name);
                                                        setEditCategoryUrl(category.selfUrl);
                                                    }}
                                                >
                                                </i>
                                                <i
                                                    className="bi bi-trash-fill default text-danger clickable-object"
                                                    data-bs-toggle="modal"
                                                    data-bs-target="#delete-category-modal"
                                                    onClick={() => setDeleteCategoryUrl(category.selfUrl)}
                                                >
                                                </i>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="product-container">
                                        {
                                            products[i].data.map((product, i) => (
                                                <ProductCard
                                                    key={i}
                                                    productId={product.productId}
                                                    name={product.name}
                                                    description={product.description}
                                                    price={product.price}
                                                    imageUrl={product.imageUrl}
                                                    edit={true}
                                                />
                                            ))
                                        }
                                        <div className="clickable-object" data-bs-toggle="modal" data-bs-target="#add-product-modal" onClick={() => setAddProductUrl(category.productsUrl)}>
                                            <div className="card add-button">
                                                <div className="card-body d-flex justify-content-center flex-column align-items-center gap-2">
                                                    <i className="bi bi-plus-circle-fill default"></i>
                                                    <span>{t("restaurant.edit.add_product")}</span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </React.Fragment>
                            ))
                        }
                        <div className="clickable-object" data-bs-toggle="modal" data-bs-target="#add-category-modal">
                            <div className="card mb-4 add-button">
                                <div className="card-body d-flex justify-content-center align-items-center gap-2">
                                    <i className="bi bi-plus-circle-fill default"></i>
                                    <span>{t("restaurant.edit.add_category")}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div className="modal fade" id="add-category-modal" tabIndex="-1">
                    <div className="modal-dialog modal-dialog-centered">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h1 className="modal-title fs-5">{t("restaurant.edit.add_category")}</h1>
                                <button type="button" className="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <Formik
                                innerRef={addCategoryFormRef}
                                initialValues={{
                                    name: ""
                                }}
                                validationSchema={AddCategorySchema}
                                onSubmit={handleAddCategory}
                            >
                                {({isSubmitting}) => (
                                    <Form>
                                        <div className="modal-body">
                                            {showAddCategoryError &&
                                                <div className="alert alert-danger" role="alert">{t("restaurant.edit.error")}</div>}
                                            <div>
                                                <label htmlFor="name" className="form-label">{t("restaurant.edit.category_name")}</label>
                                                <Field name="name" type="text" className="form-control" id="name"/>
                                                <ErrorMessage name="name" component="div" className="form-error"/>
                                            </div>
                                        </div>
                                        <div className="modal-footer">
                                            <button type="submit" className="btn btn-primary" disabled={isSubmitting}>{t("restaurant.edit.add_category")}</button>
                                        </div>
                                    </Form>
                                )}
                            </Formik>
                        </div>
                    </div>
                </div>

                <div className="modal fade" id="add-product-modal" tabIndex="-1">
                    <div className="modal-dialog modal-dialog-centered">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h1 className="modal-title fs-5">{t("restaurant.edit.add_product")}</h1>
                                <button type="button" className="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <Formik
                                innerRef={addProductFormRef}
                                initialValues={{
                                    productName: "",
                                    description: "",
                                    price: 1.00,
                                    image: ""
                                }}
                                validationSchema={AddProductSchema}
                                onSubmit={handleAddProduct}
                            >
                                {({values, isSubmitting}) => (
                                    <Form>
                                        <div className="modal-body">
                                            {showAddProductError &&
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
                                                        <img src={values.image ? URL.createObjectURL(values.image) : ImagePlaceholder} alt={values.productName || t("restaurant.edit.product_name")} className="img-fluid rounded-start"/>
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
                                            <button type="submit" className="btn btn-primary" disabled={isSubmitting}>{t("restaurant.edit.add_product")}</button>
                                        </div>
                                    </Form>
                                )}
                            </Formik>
                        </div>
                    </div>
                </div>

                <div className="modal fade" id="delete-restaurant-modal" tabIndex="-1">
                    <div className="modal-dialog modal-dialog-centered">
                        <div className="modal-content">
                            <div className="modal-body">
                                {showDeleteRestaurantError &&
                                    <div className="alert alert-danger" role="alert">{t("restaurant.edit.error")}</div>}
                                <h1 className="modal-title fs-5">{t("restaurant.edit.delete_restaurant")}</h1>
                            </div>
                            <div className="modal-footer">
                                <button type="button" data-bs-dismiss="modal" className="btn btn-secondary">{t("paging.no")}</button>
                                <button type="button" className="btn btn-danger" onClick={deleteRestaurantMutation.mutate}>{t("paging.yes")}</button>
                            </div>
                        </div>
                    </div>
                </div>

                <div className="modal fade" id="edit-information-modal" tabIndex="-1">
                    <div className="modal-dialog modal-dialog-centered">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h1 className="modal-title fs-5">{t("restaurant.edit.edit_information")}</h1>
                                <button type="button" className="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <Formik
                                innerRef={editRestaurantInformationFormRef}
                                initialValues={{
                                    name: restaurant.name,
                                    address: restaurant.address,
                                    specialty: restaurant.specialty,
                                    tags: restaurant.tags,
                                    description: restaurant.description,
                                    maxTables: restaurant.maxTables,
                                    logo: "",
                                    portrait1: "",
                                    portrait2: ""
                                }}
                                validationSchema={() => CreateRestaurantSchema(true)}
                                onSubmit={handleEditRestaurantInformation}
                                enableReinitialize={true}
                            >
                                {({isSubmitting}) => (
                                    <Form>
                                        <div className="modal-body">
                                            {showEditRestaurantInformationError &&
                                                <div className="alert alert-danger" role="alert">{t("restaurant.edit.error")}</div>}
                                            <div className="mb-3">
                                                <label htmlFor="name" className="form-label">{t("create_restaurant.name")}</label>
                                                <Field name="name" type="text" className="form-control" id="name"/>
                                                <ErrorMessage name="name" className="form-error" component="div"/>
                                            </div>
                                            <div className="mb-3">
                                                <label htmlFor="address" className="form-label">{t("create_restaurant.address")}</label>
                                                <Field name="address" type="text" className="form-control" id="address"/>
                                                <ErrorMessage name="address" className="form-error" component="div"/>
                                            </div>
                                            <div className="mb-3">
                                                <label htmlFor="specialty" className="form-label">{t("create_restaurant.specialty")}</label>
                                                <Field name="specialty">
                                                    {({field, form}) => (
                                                        <Select
                                                            placeholder={t("create_restaurant.specialty_placeholder")}
                                                            styles={selectStyles(false, false)}
                                                            components={selectComponents}
                                                            options={specialties}
                                                            closeMenuOnSelect={true}
                                                            id="specialty"
                                                            name={field.name}
                                                            value={specialties.find(option => option.value === field.value)}
                                                            onChange={option => form.setFieldValue(field.name, option.value)}
                                                            onBlur={field.onBlur}
                                                        />
                                                    )}
                                                </Field>
                                                <ErrorMessage name="specialty" className="form-error" component="div"/>
                                            </div>
                                            <div className="mb-3">
                                                <label htmlFor="tags" className="form-label">{t("create_restaurant.tags")}</label>
                                                <Field name="tags">
                                                    {({field, form}) => (
                                                        <Select
                                                            placeholder={t("create_restaurant.tags_placeholder")}
                                                            styles={selectStyles(false, false)}
                                                            components={selectComponents}
                                                            options={tags}
                                                            closeMenuOnSelect={false}
                                                            isMulti
                                                            id="tags"
                                                            name={field.name}
                                                            value={tags.filter(option => field.value.includes(option.value))}
                                                            onChange={options => form.setFieldValue(field.name, options.map(option => option.value))}
                                                            onBlur={field.onBlur}
                                                        />
                                                    )}
                                                </Field>
                                                <ErrorMessage name="tags" className="form-error" component="div"/>
                                            </div>
                                            <div className="mb-3">
                                                <label htmlFor="description" className="form-label">{t("create_restaurant.description")}</label>
                                                <Field as="textarea" name="description" className="form-control" id="description" rows="3"/>
                                                <ErrorMessage name="description" className="form-error" component="div"/>
                                            </div>
                                            <div className="mb-3">
                                                <label htmlFor="maxTables" className="form-label">{t("create_restaurant.tables_quantity")}</label>
                                                <Field name="maxTables" type="number" className="form-control" id="maxTables"/>
                                                <ErrorMessage name="maxTables" className="form-error" component="div"/>
                                            </div>
                                            <div className="mb-3">
                                                <label htmlFor="logo" className="form-label">{t("restaurant.edit.change_logo")}</label>
                                                <Field name="logo">
                                                    {({field: {value, onChange, ...field}, form}) => (
                                                        <input
                                                            type="file"
                                                            className="form-control"
                                                            id="logo"
                                                            accept="image/*"
                                                            {...field}
                                                            onChange={event => form.setFieldValue(field.name, event.currentTarget.files[0])}
                                                        />
                                                    )}
                                                </Field>
                                                <ErrorMessage name="logo" className="form-error" component="div"/>
                                            </div>
                                            <div className="mb-3">
                                                <label htmlFor="portrait1" className="form-label">{t("restaurant.edit.change_portrait1")}</label>
                                                <Field name="portrait1">
                                                    {({field: {value, onChange, ...field}, form}) => (
                                                        <input
                                                            type="file"
                                                            className="form-control"
                                                            id="portrait1"
                                                            accept="image/*"
                                                            {...field}
                                                            onChange={event => form.setFieldValue(field.name, event.currentTarget.files[0])}
                                                        />
                                                    )}
                                                </Field>
                                                <ErrorMessage name="portrait1" className="form-error" component="div"/>
                                            </div>
                                            <div className="mb-3">
                                                <label htmlFor="portrait2" className="form-label">{t("restaurant.edit.change_portrait2")}</label>
                                                <Field name="portrait2">
                                                    {({field: {value, onChange, ...field}, form}) => (
                                                        <input
                                                            type="file"
                                                            className="form-control"
                                                            id="portrait2"
                                                            accept="image/*"
                                                            {...field}
                                                            onChange={event => form.setFieldValue(field.name, event.currentTarget.files[0])}
                                                        />
                                                    )}
                                                </Field>
                                                <ErrorMessage name="portrait2" className="form-error" component="div"/>
                                            </div>
                                        </div>
                                        <div className="modal-footer">
                                            <button type="submit" className="btn btn-primary" disabled={isSubmitting}>{t("restaurant.edit.edit_information")}</button>
                                        </div>
                                    </Form>
                                )}
                            </Formik>
                        </div>
                    </div>
                </div>

                <div className="modal fade" id="edit-category-modal" tabIndex="-1">
                    <div className="modal-dialog modal-dialog-centered">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h1 className="modal-title fs-5">{t("restaurant.edit.edit_category")}</h1>
                                <button type="button" className="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <Formik
                                innerRef={editCategoryFormRef}
                                initialValues={{
                                    name: editCategoryName
                                }}
                                validationSchema={AddCategorySchema}
                                onSubmit={handleEditCategory}
                                enableReinitialize={true}
                            >
                                {({isSubmitting}) => (
                                    <Form>
                                        <div className="modal-body">
                                            {showEditCategoryError &&
                                                <div className="alert alert-danger" role="alert">{t("restaurant.edit.error")}</div>}
                                            <div>
                                                <label htmlFor="name" className="form-label">{t("restaurant.edit.category_name")}</label>
                                                <Field name="name" type="text" className="form-control" id="name"/>
                                                <ErrorMessage name="name" component="div" className="form-error"/>
                                            </div>
                                        </div>
                                        <div className="modal-footer">
                                            <button type="submit" className="btn btn-primary" disabled={isSubmitting}>{t("restaurant.edit.edit_category")}</button>
                                        </div>
                                    </Form>
                                )}
                            </Formik>
                        </div>
                    </div>
                </div>

                <div className="modal fade" id="delete-category-modal" tabIndex="-1">
                    <div className="modal-dialog modal-dialog-centered">
                        <div className="modal-content">
                            <div className="modal-body">
                                {showDeleteCategoryError &&
                                    <div className="alert alert-danger" role="alert">{t("restaurant.edit.error")}</div>}
                                <h1 className="modal-title fs-5">{t("restaurant.edit.delete_category")}</h1>
                            </div>
                            <div className="modal-footer">
                                <button type="button" data-bs-dismiss="modal" className="btn btn-secondary">{t("paging.no")}</button>
                                <button type="button" className="btn btn-danger" onClick={deleteCategoryMutation.mutate}>{t("paging.yes")}</button>
                            </div>
                        </div>
                    </div>
                </div>

                <div className="modal fade" id="edit-employees-modal" tabIndex="-1">
                    <div className="modal-dialog modal-dialog-centered modal-lg modal-dialog-scrollable">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h1 className="modal-title fs-5">{t("restaurant.edit.edit_employees")}</h1>
                                <button type="button" className="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div className="modal-body">
                                <h4>{t("restaurant.edit.add_employee")}</h4>
                                {showAddEmployeeError &&
                                    <div className="alert alert-danger" role="alert">{t("restaurant.edit.error")}</div>}
                                <Formik
                                    innerRef={addEmployeeFormRef}
                                    initialValues={{
                                        email: "",
                                        role: ROLE_FOR_RESTAURANT.ORDER_HANDLER
                                    }}
                                    validationSchema={AddEmployeeSchema}
                                    onSubmit={handleAddEmployee}
                                >
                                    {({isSubmitting}) => (
                                        <Form>
                                            <div className="mb-3">
                                                <label htmlFor="email" className="form-label">{t("restaurant.edit.employee_email")}</label>
                                                <Field name="email" type="email" className="form-control" id="email"/>
                                                <ErrorMessage name="email" component="div" className="form-error"/>
                                            </div>
                                            <div className="mb-3">
                                                <label htmlFor="role" className="form-label">{t("restaurant.edit.employee_role")}</label>
                                                <Field as="select" name="role" className="form-select" aria-labelledby="role-help-text">
                                                    <option value={ROLE_FOR_RESTAURANT.ADMIN}>{t(`restaurant.edit.roles.${ROLE_FOR_RESTAURANT.ADMIN}`)}</option>
                                                    <option value={ROLE_FOR_RESTAURANT.ORDER_HANDLER}>{t(`restaurant.edit.roles.${ROLE_FOR_RESTAURANT.ORDER_HANDLER}`)}</option>
                                                </Field>
                                                <ErrorMessage name="role" component="div" className="form-error"/>
                                                <div id="role-help-text" className="form-text">{t("restaurant.edit.employee_role_help_text")}</div>
                                            </div>
                                            <button type="submit" className="btn btn-primary" disabled={isSubmitting}>{t("restaurant.edit.add_employee")}</button>
                                        </Form>
                                    )}
                                </Formik>
                                <div className="mt-4">
                                    <h4>{t("restaurant.edit.restaurant_employees")}</h4>
                                    {showEditEmployeeRoleError &&
                                        <div className="alert alert-danger" role="alert">{t("restaurant.edit.error")}</div>}
                                    <ul className="list-group list-group-flush">
                                        {
                                            employees.map((employee, i) => (
                                                <li className="list-group-item d-flex align-items-center" key={i}>
                                                    <i className="bi bi-person me-3"></i>
                                                    <div className="d-flex justify-content-between align-items-center w-100">
                                                        <p className="mb-0">
                                                            <span>{employee.name} </span>
                                                            <a href={`mailto:${employee.email}`}>&lt;{employee.email}&gt;</a>
                                                        </p>
                                                        <div className="d-flex align-items-center">
                                                            {
                                                                (employee.role !== ROLE_FOR_RESTAURANT.OWNER && employee.userUrl !== authContext.selfUrl)
                                                                    ?
                                                                    <>
                                                                        {
                                                                            editEmployeeRoleUrl === employee.selfUrl
                                                                                ?
                                                                                <Formik
                                                                                    innerRef={editEmployeeRoleFormRef}
                                                                                    initialValues={{
                                                                                        role: employee.role
                                                                                    }}
                                                                                    validationSchema={EditEmployeeRoleSchema}
                                                                                    onSubmit={handleEditEmployeeRole}
                                                                                >
                                                                                    {({handleSubmit}) => (
                                                                                        <Form>
                                                                                            <div className="d-flex align-items-center gap-3 ms-3">
                                                                                                <Field as="select" name="role" className="form-select">
                                                                                                    <option value={ROLE_FOR_RESTAURANT.ADMIN}>{t(`restaurant.edit.roles.${ROLE_FOR_RESTAURANT.ADMIN}`)}</option>
                                                                                                    <option value={ROLE_FOR_RESTAURANT.ORDER_HANDLER}>{t(`restaurant.edit.roles.${ROLE_FOR_RESTAURANT.ORDER_HANDLER}`)}</option>
                                                                                                </Field>
                                                                                                <ErrorMessage name="role" component="div" className="form-error d-none"/>
                                                                                                <i
                                                                                                    className="bi bi-check-circle-fill text-success default clickable-object"
                                                                                                    onClick={() => handleSubmit()}
                                                                                                >
                                                                                                </i>
                                                                                                <i
                                                                                                    className="bi bi-trash-fill text-danger default clickable-object"
                                                                                                    data-bs-toggle="modal"
                                                                                                    data-bs-target="#delete-employee-modal"
                                                                                                    onClick={() => setDeleteEmployeeUrl(employee.selfUrl)}
                                                                                                >
                                                                                                </i>
                                                                                            </div>
                                                                                        </Form>
                                                                                    )}
                                                                                </Formik>
                                                                                :
                                                                                <>
                                                                                    <p className="mb-0">{t(`restaurant.edit.roles.${employee.role}`)}</p>
                                                                                    <div className="d-flex align-items-center gap-3 ms-3">
                                                                                        <i
                                                                                            className="bi bi-pencil-fill clickable-object"
                                                                                            onClick={() => setEditEmployeeRoleUrl(employee.selfUrl)}
                                                                                        >
                                                                                        </i>
                                                                                        <i
                                                                                            className="bi bi-trash-fill text-danger default clickable-object"
                                                                                            data-bs-toggle="modal"
                                                                                            data-bs-target="#delete-employee-modal"
                                                                                            onClick={() => setDeleteEmployeeUrl(employee.selfUrl)}
                                                                                        >
                                                                                        </i>
                                                                                    </div>
                                                                                </>
                                                                        }
                                                                    </>
                                                                    :
                                                                    <p className="mb-0">{t(`restaurant.edit.roles.${employee.role}`)}</p>
                                                            }
                                                        </div>
                                                    </div>
                                                </li>
                                            ))
                                        }
                                    </ul>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div className="modal fade" id="delete-employee-modal" tabIndex="-1">
                    <div className="modal-dialog modal-dialog-centered">
                        <div className="modal-content">
                            <div className="modal-body">
                                {showDeleteEmployeeError &&
                                    <div className="alert alert-danger" role="alert">{t("restaurant.edit.error")}</div>}
                                <h1 className="modal-title fs-5">{t("restaurant.edit.delete_employee")}</h1>
                            </div>
                            <div className="modal-footer">
                                <button type="button" data-bs-toggle="modal" data-bs-target="#edit-employees-modal" className="btn btn-secondary">{t("paging.no")}</button>
                                <button type="button" className="btn btn-danger" onClick={deleteEmployeeMutation.mutate}>{t("paging.yes")}</button>
                            </div>
                        </div>
                    </div>
                </div>
            </Page>
        </>
    );
}

export default EditRestaurant;
