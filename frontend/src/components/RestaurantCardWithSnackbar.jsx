import { Link } from "react-router-dom";
import "./styles/restaurant_card.styles.css";

function RestaurantCardWithSnackbar({ restaurantId, mainImage, hoverImage, name, address, snackbarText, isSnackbarSuccess, isSnackbarDanger, clickable = true, ...rest }) {
    const card = (
        <div className="card">
            <div
                className="card-img"
                style={{"--main_image": `url(${mainImage})`, "--hover_image": `url(${hoverImage})`}}
            />
            <div className="card-body">
                <div>
                    <h5 className="card-title">{name}</h5>
                    <p className="card-text">{address}</p>
                </div>
            </div>
            <h4>
                <span className={`position-absolute top-0 start-50 translate-middle badge rounded-pill ${isSnackbarSuccess ? "bg-success" : isSnackbarDanger ? "bg-danger" : "bg-secondary"}`}>
                    {snackbarText}
                </span>
            </h4>
        </div>
    );

    return (
        <div className="restaurant_card">
            {
                clickable
                    ?
                    <Link className="clickable-object position-relative" to={`/restaurants/${restaurantId}`} {...rest}>
                        {card}
                    </Link>
                    :
                    <div className="clickable-object" {...rest}>
                        {card}
                    </div>
            }
        </div>
    );
}

export default RestaurantCardWithSnackbar;
