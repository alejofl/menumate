import {useContext, useEffect} from "react";
import AuthContext from "../contexts/AuthContext.jsx";
import {useLocation, useNavigate} from "react-router-dom";

function ProtectedRoute({children}) {
    const location = useLocation();
    const navigate = useNavigate();
    const authContext = useContext(AuthContext);

    useEffect(() => {
        if (!authContext.isAuthenticated) {
            return navigate(`/auth/login?next=${location.pathname}`);
        }
    }, [authContext.isAuthenticated]);

    return (
        <>
            {children}
        </>
    );
}

export default ProtectedRoute;
