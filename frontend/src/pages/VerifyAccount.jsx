import {useNavigate, useSearchParams} from "react-router-dom";
import {useContext, useEffect, useRef} from "react";
import AuthContext from "../contexts/AuthContext.jsx";

function VerifyAccount() {
    const navigate = useNavigate();
    const authContext = useContext(AuthContext);

    const countdownLatch = useRef(false);
    const [queryParams] = useSearchParams();

    useEffect(() => {
        if (countdownLatch.current) {
            return;
        }

        if (authContext.isAuthenticated) {
            return navigate("/");
        }

        if (!queryParams.has("token") || !queryParams.has("email")) {
            return navigate("/auth/login?alertType=danger&alertMessage=login.request_error");
        }

        const errorUrl = "/auth/login?alertType=danger&alertMessage=login.verify_error";
        authContext.login(queryParams.get("email"), queryParams.get("token"), false)
            .then(success => {
                if (!success) {
                    return navigate(errorUrl);
                } else {
                    return navigate("/");
                }
            })
            .catch(() => navigate(errorUrl));

        countdownLatch.current = true;
    }, []);

    return (
        <>
        </>
    );
}

export default VerifyAccount;
