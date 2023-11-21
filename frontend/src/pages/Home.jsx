import { useTranslation } from "react-i18next";
import Page from "../components/Page.jsx";
import RestaurantCard from "../components/RestaurantCard.jsx";
import { Link } from "react-router-dom";
import "./styles/home.styles.css";

function Home() {
    const { t } = useTranslation();

    return (
        <>
            <Page title={t("titles.home")}>
                <div className="landing-container">
                    <p className="landing-title">{t("home.call_to_action")}</p>
                    <form method="get" action="/restaurants">
                        <div className="search-form-container">
                            <div>
                                <div className="input-group flex-nowrap">
                                    <span className="input-group-text search-input"><i className="bi bi-search"></i></span>
                                    <input type="text" name="search" className="form-control search-input" placeholder={t("restaurants.search_placeholder")}/>
                                </div>
                            </div>
                        </div>
                        <input type="submit" className="btn btn-primary" value={t("restaurants.search_button")}/>
                    </form>
                </div>
                <div className="landing-restaurants">
                    <p className="landing-restaurants-title">{t("home.restaurants_title")}</p>
                    <div className="landing-restaurants-feed">
                        <RestaurantCard
                            restaurantId={1}
                            name="Prueba"
                            mainImage="http://pawserver.it.itba.edu.ar/paw-2023a-01/images/1"
                            hoverImage="http://pawserver.it.itba.edu.ar/paw-2023a-01/images/2"
                            address="Edison 439, Martinez"
                            rating={2}
                            ratingCount={5}
                            tags={["elegant", "old_fashioned", "lgbt_friendly"]}
                        />
                        <RestaurantCard
                            restaurantId={1}
                            name="La Parolaccio Casa Tua Palermo"
                            mainImage="http://pawserver.it.itba.edu.ar/paw-2023a-01/images/1"
                            hoverImage="http://pawserver.it.itba.edu.ar/paw-2023a-01/images/2"
                            address="Edison 439, Martinez"
                            rating={2}
                            ratingCount={5}
                            tags={["elegant", "old_fashioned", "lgbt_friendly"]}
                        />
                        <RestaurantCard
                            restaurantId={1}
                            name="Prueba"
                            mainImage="http://pawserver.it.itba.edu.ar/paw-2023a-01/images/1"
                            hoverImage="http://pawserver.it.itba.edu.ar/paw-2023a-01/images/2"
                            address="Edison 439, Martinez"
                            rating={2}
                            ratingCount={5}
                            tags={["elegant", "old_fashioned", "lgbt_friendly"]}
                        />
                        <RestaurantCard
                            restaurantId={1}
                            name="Prueba"
                            mainImage="http://pawserver.it.itba.edu.ar/paw-2023a-01/images/1"
                            hoverImage="http://pawserver.it.itba.edu.ar/paw-2023a-01/images/2"
                            address="Edison 439, Martinez"
                            rating={2}
                            ratingCount={5}
                            tags={["elegant", "old_fashioned", "lgbt_friendly"]}
                        />
                    </div>
                    <Link to="/restaurants" className="btn btn-primary">{t("home.restaurants_button")}</Link>
                </div>
            </Page>
        </>
    );
}

export default Home;
