import { useEffect } from "react";
import Api from "../data/Api.js";

export function useApi() {
    useEffect(() => {
        const requestInterceptor = Api.interceptors.request.use(
            (config) => {
                // TODO add Authorization header
                return config;
            },
            (error) => Promise.reject(error)
        );

        const responseInterceptor = Api.interceptors.response.use(
            (response) => response,
            (error) => {
                // TODO check if error is 401 and reauthenticate if necessary
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
