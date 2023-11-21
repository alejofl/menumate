import { useTranslation } from "react-i18next";
import "./styles/tags_container.styles.css";
import { Link } from "react-router-dom";

function TagsContainer({ tags, clickable = false }) {
    const { t } = useTranslation();

    return (
        <>
            <div className="tags-container">
                {tags.map(tag => (
                    clickable
                        ?
                        <Link className="clickable-object" to="/restaurants" key={tag}>
                            <span className="badge rounded-pill text-bg-secondary">
                                {t(`restaurant_tags.${tag}`)}
                            </span>
                        </Link>
                        :
                        <span className="badge rounded-pill text-bg-secondary" key={tag}>
                            {t(`restaurant_tags.${tag}`)}
                        </span>
                ))}
            </div>
        </>
    );
}

export default TagsContainer;
