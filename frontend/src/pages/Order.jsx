import Page from "../components/Page.jsx";
import {useTranslation} from "react-i18next";

function Order() {
    const { t } = useTranslation();

    return (
        <>
            <Page title={t("titles.home")} className="order">
            </Page>
        </>
    );
}

export default Order;
