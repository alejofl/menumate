import i18n from "i18next";
import LanguageDetector from "i18next-browser-languagedetector";
import { initReactI18next } from "react-i18next";
import englishTranslations from "./locale/en.json";
import spanishTranslations from "./locale/es.json";

const resources = {
    en: {
        translation: englishTranslations
    },
    es: {
        translation: spanishTranslations
    }
};

i18n
    .use(LanguageDetector)
    .use(initReactI18next)
    .init({
        detection: {
            order: ["querystring", "navigator"],
            lookupQuerystring: "lng"
        },
        resources: resources,
        fallbackLng: "en",
        interpolation: {
            escapeValue: false
        }
    });

export default i18n;
