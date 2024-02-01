import { Link } from "react-router-dom";
import "./styles/restaurant_card.styles.css";
import Rating from "./Rating.jsx";
import TagsContainer from "./TagsContainer.jsx";
import ImagePlaceholder from "../assets/image-placeholder.png";

function RestaurantCard({ restaurantId, mainImage, hoverImage, name, address, rating, ratingCount, tags }) {
    return (
        <div className="restaurant_card">
            <Link className="clickable-object" to={`/restaurants/${restaurantId}`}>
                <div className="card">
                    <div
                        className="card-img"
                        style={{"--main_image": `url(${mainImage || ImagePlaceholder})`, "--hover_image": `url(${hoverImage || ImagePlaceholder})`}}
                    />
                    <div className="card-body">
                        <div>
                            <h5 className="card-title">{name}</h5>
                            <p className="card-text">{address}</p>
                        </div>
                        <div>
                            <Rating rating={rating} count={ratingCount}/>
                            <TagsContainer tags={tags}/>
                        </div>
                    </div>
                </div>
            </Link>
        </div>
    );
}

export default RestaurantCard;
