import "./styles/product_card.styles.css";
import ImagePlaceholder from "../assets/image-placeholder.png";
import {useTranslation} from "react-i18next";

function ProductCard({productId, name, description, price, discount, imageUrl}) {
    const { t } = useTranslation();

    return (
        <div className="product_card" data-x={productId}>
            <a className="clickable-object">
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
    );
}

export default ProductCard;
