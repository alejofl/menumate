import "./styles/product_card.styles.css";
import ImagePlaceholder from "../assets/image-placeholder.png";
import {useTranslation} from "react-i18next";
import {useEffect, useState} from "react";
import {PRICE_DECIMAL_DIGITS} from "../utils.js";

function ProductCard({productId, name, description, price, discount, imageUrl, addProductToCart, disabled}) {
    const DECREMENT_QUANTITY = -1;
    const INCREMENT_QUANTITY = 1;
    const MAX_QUANTITY = 100;
    const MIN_QUANTITY = 1;
    const MAX_COMMENT_LENGTH = 120;

    const { t } = useTranslation();

    const [quantity, setQuantity] = useState(1);
    const [comments, setComments] = useState("");

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
        document.addEventListener("hidden.bs.modal", () => {
            setQuantity(1);
            setComments("");
        });

        return () => {
            document.removeEventListener("hidden.bs.modal", null);
        };
    });

    return (
        <>
            <div className="product_card">
                <a className="clickable-object" data-bs-toggle="modal" data-bs-target={`#add-${productId}-to-cart`}>
                    <div className="card">
                        <div className="image-container">
                            <img src={imageUrl || ImagePlaceholder} alt={name} className="img-fluid rounded-start"/>
                        </div>
                        <div className="card-body">
                            <div>
                                <p className="card-text">{name}</p>
                                <p className="card-text">
                                    <small className="text-body-secondary">{description || t("restaurant.product.no_description")}</small>
                                </p>
                            </div>
                            <h5 className="card-title">
                                ${price}
                                {discount && <span className="badge bg-promotion ms-2">-{discount}%</span>}
                            </h5>
                        </div>
                    </div>
                </a>
            </div>

            <div className="modal fade add-product-to-cart" id={`add-${productId}-to-cart`} tabIndex="-1" >
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
        </>
    );
}

export default ProductCard;
