import { useTranslation } from "react-i18next";
import "./styles/footer.styles.css";

function Footer() {
    const { t } = useTranslation();

    return (
        <div className="footer">
            <footer>
                <small>{t("copyright")}</small>
            </footer>
        </div>
    );
}

export default Footer;
