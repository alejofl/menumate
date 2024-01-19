import {useContext, useEffect} from "react";
import Api from "../data/Api.js";
import AuthContext from "../contexts/AuthContext.jsx";
import {UNAUTHORIZED_STATUS_CODE} from "../utils.js";

export function useApi() {
    const authContext = useContext(AuthContext);

    useEffect(() => {
        const requestInterceptor = Api.interceptors.request.use(
            (config) => {
                if (authContext.isAuthenticated) {
                    config.headers["Authorization"] = `Bearer ${authContext.jwt}`;
                }
                return config;
            },
            (error) => Promise.reject(error)
        );

        const responseInterceptor = Api.interceptors.response.use(
            (response) => response,
            (error) => {
                if (error?.response?.status === UNAUTHORIZED_STATUS_CODE) {
                    authContext.logout();
                    /*
                     * TODO FIX THIS
                     * const previousRequest = error?.config;
                     * previousRequest.headers["Authorization"] = `Bearer ${authContext.refreshToken}`;
                     * return Api.request(previousRequest)
                     *     .then((response) => {
                     *         if (response.headers["x-menumate-authtoken"] && response.headers["x-menumate-refreshtoken"]) {
                     *             authContext.updateTokens(response.headers["x-menumate-authtoken"], response.headers["x-menumate-refreshtoken"]);
                     *         }
                     *         return response;
                     *     })
                     *     .catch((error) => {
                     *         if (error?.response?.status === UNAUTHORIZED_STATUS_CODE) {
                     *             authContext.logout();
                     *         }
                     *         throw error;
                     *     });
                     */
                }
                return Promise.reject(error);
            }
        );

        return () => {
            Api.interceptors.request.eject(requestInterceptor);
            Api.interceptors.response.eject(responseInterceptor);
        };
    });
    return Api;
}
