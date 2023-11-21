import { useTranslation } from "react-i18next";
import Page from "../components/Page.jsx";

function Home() {
    const { t } = useTranslation();

    return (
        <>
            <Page title={t("titles.home")}>
                <h1>Hola, probando</h1>
            </Page>
        </>
    );
}

export default Home;
