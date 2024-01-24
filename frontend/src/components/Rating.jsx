import { useTranslation } from "react-i18next";
import "./styles/rating.styles.css";

function Rating({ rating, count, showText = true }) {
    const { t } = useTranslation();
    const STAR_COUNT = 5;
    const ratingNumber = parseInt(rating);

    return (
        <div className="rating">
            <div className="small-ratings">
                {[...Array(ratingNumber)].map((_, index) => <i className="bi bi-star-fill rating-color" key={index}></i>)}
                {[...Array(STAR_COUNT - ratingNumber)].map((_, index) => <i className="bi bi-star-fill" key={index}></i>)}
                {
                    showText &&
                    <small className="text-muted ps-1">
                        {t("restaurant_card.rating_text", {count: count || 0})}
                    </small>
                }
            </div>
        </div>
    );
}

export default Rating;
