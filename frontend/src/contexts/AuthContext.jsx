/* eslint-disable no-empty-function */
import React, {useState} from "react";
import {jwtDecode} from "jwt-decode";
import {useApi} from "../hooks/useApi.js";
import {useUserService} from "../hooks/services/useUserService.js";

const AuthContext = React.createContext({
    isAuthenticated: false,
    jwt: null,
    refreshToken: null,
    name: null,
    role: null,
    selfUrl: null,
    login: () => {},
    logout: () => {},
    updateTokens: () => {}
});

export function AuthContextProvider({children}) {
    const api = useApi();
    const userService = useUserService(api);

    const jwtParamSetter = (param, defaultValue, token = jwt) => {
        if (token) {
            return jwtDecode(token)[param] || defaultValue;
        }
        return null;
    };

    const [isAuthenticated, setIsAuthenticated] = useState(localStorage.getItem("jwt") !== null || sessionStorage.getItem("jwt") !== null);
    const [jwt, setJwt] = useState(localStorage.getItem("jwt") || sessionStorage.getItem("jwt") || null);
    const [refreshToken, setRefreshToken] = useState(localStorage.getItem("refreshToken") || sessionStorage.getItem("refreshToken") || null);
    const [name, setName] = useState(jwtParamSetter("name", null));
    const [role, setRole] = useState(jwtParamSetter("role", "USER"));
    const [selfUrl, setSelfUrl] = useState(jwtParamSetter("selfUrl", null));

    const loginHandler = async (email, password, rememberMe) => {
        const response = await userService.login("/", email, password);
        if (response.success) {
            setIsAuthenticated(true);
            setJwt(response.jwt);
            setRefreshToken(response.refreshToken);
            setName(jwtParamSetter("name", null, response.jwt));
            setRole(jwtParamSetter("role", "USER", response.jwt));
            setSelfUrl(jwtParamSetter("selfUrl", null, response.jwt));
            if (rememberMe) {
                localStorage.setItem("jwt", response.jwt);
                localStorage.setItem("refreshToken", response.refreshToken);
            } else {
                sessionStorage.setItem("jwt", response.jwt);
                sessionStorage.setItem("refreshToken", response.refreshToken);
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

    const updateTokensHandler = (kwt, refreshToken) => {
        setJwt(jwt);
        setRefreshToken(refreshToken);
        if (localStorage.getItem("jwt") !== null && localStorage.getItem("refreshToken") !== null) {
            localStorage.setItem("jwt", jwt);
            localStorage.setItem("refreshToken", refreshToken);
        } else {
            sessionStorage.setItem("jwt", jwt);
            sessionStorage.setItem("refreshToken", refreshToken);
        }
    };

    return (
        <>
            <AuthContext.Provider value={{
                isAuthenticated: isAuthenticated,
                jwt: jwt,
                refreshToken: refreshToken,
                name: name,
                role: role,
                selfUrl: selfUrl,
                login: loginHandler,
                logout: logoutHandler,
                updateTokens: updateTokensHandler
            }}>
                {children}
            </AuthContext.Provider>
        </>
    );
}

export default AuthContext;
