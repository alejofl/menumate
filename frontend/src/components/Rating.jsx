import { useTranslation } from "react-i18next";
import "./styles/rating.styles.css";

function Rating({ rating, count }) {
    const { t } = useTranslation();
    const STAR_COUNT = 5;

    return (
        <>
            <div className="small-ratings">
                {[...Array(rating)].map((_, index) => <i className="bi bi-star-fill rating-color" key={index}></i>)}
                {[...Array(STAR_COUNT - rating)].map((_, index) => <i className="bi bi-star-fill" key={index}></i>)}
                <small className="text-muted ps-1">
                    {t("restaurant_card.rating_text", {count: count})}
                </small>
            </div>
        </>
    );
}

export default Rating;
