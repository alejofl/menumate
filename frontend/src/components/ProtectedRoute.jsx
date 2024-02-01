import {useContext, useEffect} from "react";
import AuthContext from "../contexts/AuthContext.jsx";
import {useLocation, useNavigate} from "react-router-dom";
import Error from "../pages/Error.jsx";

function ProtectedRoute({moderator = false, children}) {
    const location = useLocation();
    const navigate = useNavigate();
    const authContext = useContext(AuthContext);

    useEffect(() => {
        if (!authContext.isAuthenticated) {
            return navigate(`/auth/login?next=${location.pathname}`);
        }
    }, [authContext.isAuthenticated]);

    if (moderator && !authContext.isModerator) {
        return <Error errorNumber="403"/>;
    }
    return (
        <>
            {children}
        </>
    );
}

export default ProtectedRoute;
