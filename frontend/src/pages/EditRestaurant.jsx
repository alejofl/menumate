import React, {useEffect, useRef, useState} from "react";
import Page from "../components/Page.jsx";
import {useMutation} from "@tanstack/react-query";
import {useTranslation} from "react-i18next";
import {useApi} from "../hooks/useApi.js";
import {useContext} from "react";
// import ApiContext from "../contexts/ApiContext.jsx";
import {useRestaurantService} from "../hooks/services/useRestaurantService.js";
/*
 * import Error from "./Error.jsx";
 * import ContentLoader from "react-content-loader";
 */
import "./styles/restaurant.styles.css";
import TagsContainer from "../components/TagsContainer.jsx";
import ProductCard from "../components/ProductCard.jsx";
import AuthContext from "../contexts/AuthContext.jsx";
import {Link} from "react-router-dom";
import {ErrorMessage, Field, Form, Formik} from "formik";
import {AddCategorySchema} from "../data/validation.js";

// eslint-disable-next-line no-unused-vars
function EditRestaurant({restaurant, categories, products, promotions, promotionProducts, userRole, refetchRestaurant, refetchCategories, refetchPromotions}) {
    const { t } = useTranslation();
    const api = useApi();
    // const apiContext = useContext(ApiContext);
    const authContext = useContext(AuthContext);
    const restaurantService = useRestaurantService(api);

    /*
     * const { isPending: categoriesIsPending, isError: categoriesIsError, data: categories, error: categoriesError} = useQuery({
     *     queryKey: ["restaurant", restaurantId, "categories"],
     *     queryFn: async () => (
     *         await restaurantService.getCategories(restaurant.categoriesUrl)
     *     ),
     *     enabled: !!restaurant
     * });
     */

    const addCategoryFormRef = useRef();

    const [showAddCategoryError, setShowAddCategoryError] = useState(false);

    useEffect(() => {
        document.querySelector("#add-category-modal").addEventListener("hidden.bs.modal", () => {
            addCategoryFormRef.current?.resetForm();
            setShowAddCategoryError(false);
        });
    }, []);

    const addCategoryMutation = useMutation({
        mutationFn: async ({name}) => {
            await restaurantService.addCategory(restaurant.categoriesUrl, name);
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
                onError: () => setShowAddCategoryError(false)
            }
        );
        setSubmitting(false);
    };

    /*
     * if (false) {
     *     return (
     *         <>
     *             <Page title={t("titles.loading")}>
     *                 <ContentLoader backgroundColor="#eaeaea" foregroundColor="#e0e0e0" width="1920">
     *                     <rect x="288" y="50" rx="0" ry="0" width="1344" height="200"/>
     *                     <rect x="12" y="300" rx="0" ry="0" width="200" height="380"/>
     *                     <rect x="236" y="300" rx="0" ry="0" width="1268" height="64"/>
     *                     <rect x="236" y="388" rx="0" ry="0" width="400" height="200"/>
     *                     <rect x="656" y="388" rx="0" ry="0" width="400" height="200"/>
     *                     <rect x="1528" y="300" rx="0" ry="0" width="380" height="120"/>
     *                 </ContentLoader>
     *             </Page>
     *         </>
     *     );
     * }
     */
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
                            <button className="btn btn-secondary" type="button">{t("restaurant.edit.edit_information")}</button>
                            {authContext.isAuthenticated && (userRole.isOwner) &&
                                <>
                                    <button className="btn btn-secondary" type="button">{t("restaurant.edit.edit_employees")}</button>
                                    <button className="btn btn-danger" type="button">{t("restaurant.delete_restaurant")}</button>
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
                                        <div className="clickable-object" data-bs-toggle="modal" data-bs-target="#add-item-modal">
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
                            <div className="card my-4 add-button">
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
                                            {showAddCategoryError && <div className="alert alert-danger" role="alert">{t("restaurant.edit.error")}</div>}
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
            </Page>
        </>
    );
}

export default EditRestaurant;
