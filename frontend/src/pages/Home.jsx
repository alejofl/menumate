import { useTranslation } from "react-i18next";
import Page from "../components/Page.jsx";
import RestaurantCard from "../components/RestaurantCard.jsx";

function Home() {
    const { t } = useTranslation();

    return (
        <>
            <Page title={t("titles.home")}>
                <h1>Hola, probando</h1>
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
            </Page>
        </>
    );
}

export default Home;
