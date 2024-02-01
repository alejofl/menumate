import { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Modal } from "bootstrap";

function ErrorModal({ title = undefined, message = undefined}) {
    const { t } = useTranslation();

    useEffect(() => {
        const modal = Modal.getOrCreateInstance(document.querySelector(".error_modal .modal"));
        modal.show();
    }, []);

    const handleClick = () => {
        window.location.reload();
    };

    return (
        <div className="error_modal">
            <div className="modal fade" data-bs-backdrop="static" data-bs-keyboard="false" tabIndex="-1" aria-hidden="true">
                <div className="modal-dialog modal-dialog-centered">
                    <div className="modal-content">
                        <div className="modal-header">
                            <h1 className="modal-title fs-5">{title || t("error.modal.title")}</h1>
                        </div>
                        <div className="modal-body">
                            <p>{message || t("error.modal.description")}</p>
                        </div>
                        <div className="modal-footer">
                            <button type="button" className="btn btn-danger" onClick={handleClick}>{t("error.modal.button")}</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default ErrorModal;
