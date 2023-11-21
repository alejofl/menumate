import { useTranslation } from "react-i18next";
import "./styles/footer.styles.css";

function Footer() {
    const { t } = useTranslation();

    return (
        <>
            <footer>
                <small>{t("copyright")}</small>
            </footer>
        </>
    );
}

export default Footer;
