/* eslint-disable no-empty-function */
import React, {useContext, useState} from "react";
import {jwtDecode} from "jwt-decode";
import {useApi} from "../hooks/useApi.js";
import ApiContext from "./ApiContext.jsx";
import {useUserService} from "../hooks/services/useUserService.js";

const AuthContext = React.createContext({
    isAuthenticated: false,
    jwt: null,
    name: null,
    role: null,
    selfUrl: null,
    login: () => {},
    logout: () => {}
});

export function AuthContextProvider({children}) {
    const api = useApi();
    const apiContext = useContext(ApiContext);
    const userService = useUserService(api);

    const jwtParamSetter = (param, defaultValue, token = jwt) => {
        if (token) {
            return jwtDecode(token)[param] || defaultValue;
        }
        return null;
    };

    const [isAuthenticated, setIsAuthenticated] = useState(localStorage.getItem("jwt") !== null || sessionStorage.getItem("jwt") !== null);
    const [jwt, setJwt] = useState(localStorage.getItem("jwt") || sessionStorage.getItem("jwt") || null);
    const [name, setName] = useState(jwtParamSetter("name", null));
    const [role, setRole] = useState(jwtParamSetter("role", "USER"));
    const [selfUrl, setSelfUrl] = useState(jwtParamSetter("selfUrl", null));

    const loginHandler = async (email, password, rememberMe) => {
        const response = await userService.login(apiContext.usersUrl, email, password);
        if (response.success) {
            setIsAuthenticated(true);
            setJwt(response.jwt);
            setName(jwtParamSetter("name", null, response.jwt));
            setRole(jwtParamSetter("role", "USER", response.jwt));
            setSelfUrl(jwtParamSetter("selfUrl", null, response.jwt));
            if (rememberMe) {
                localStorage.setItem("jwt", response.jwt);
            } else {
                sessionStorage.setItem("jwt", response.jwt);
            }
            return true;
        } else {
            return false;
        }
    };

    const logoutHandler = () => {
        setIsAuthenticated(false);
        setJwt(null);
        setName(null);
        setRole(null);
        setSelfUrl(null);
        localStorage.removeItem("jwt");
        sessionStorage.removeItem("jwt");
    };

    return (
        <>
            <AuthContext.Provider value={{
                isAuthenticated: isAuthenticated,
                jwt: jwt,
                name: name,
                role: role,
                selfUrl: selfUrl,
                login: loginHandler,
                logout: logoutHandler
            }}>
                {children}
            </AuthContext.Provider>
        </>
    );
}

export default AuthContext;
