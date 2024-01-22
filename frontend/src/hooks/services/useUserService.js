import {
    REVIEW_CONTENT_TYPE,
    NOT_FOUND_STATUS_CODE,
    RESTAURANT_EMPLOYEES_CONTENT_TYPE,
    USER_ADDRESS_CONTENT_TYPE,
    USER_CONTENT_TYPE,
    USER_PASSWORD_CONTENT_TYPE
} from "../../utils.js";
import User from "../../data/model/User.js";
import Address from "../../data/model/Address.js";
import Review from "../../data/model/Review.js";
import {parseLinkHeader} from "@web3-storage/parse-link-header";
import PagedContent from "../../data/model/PagedContent.js";
import UserRoleForRestaurant from "../../data/model/UserRoleForRestaurant.js";

export function useUserService(api) {
    const sendResetPasswordToken = async (url, email) => {
        return await api.post(
            url,
            {
                email: email
            },
            {
                headers: {
                    "Content-Type": USER_PASSWORD_CONTENT_TYPE
                }
            }
        );
    };

    const resetPassword = async (url, newPassword) => {
        return await api.patch(
            url,
            {
                password: newPassword
            },
            {
                headers: {
                    "Content-Type": USER_PASSWORD_CONTENT_TYPE
                }
            });
    };

    const login = async (url, email, password) => {
        const response = await api.get(url, {
            headers: {
                "Accept": USER_CONTENT_TYPE,
                "Authorization": `Basic ${btoa(`${email}:${password}`)}`
            }
        });
        if (response.headers["x-menumate-authtoken"] && response.headers["x-menumate-refreshtoken"]) {
            return {
                success: true,
                jwt: response.headers["x-menumate-authtoken"],
                refreshToken: response.headers["x-menumate-refreshtoken"]
            };
        } else {
            return {success: false, jwt: null, refreshToken: null};
        }
    };

    const register = async (url, name, email, password, language) => {
        return await api.post(
            url,
            {
                name: name,
                email: email,
                password: password
            },
            {
                headers: {
                    "Content-Type": USER_CONTENT_TYPE,
                    "Accept-Language": language
                }
            }
        );
    };

    const getUser = async (url) => {
        const response = await api.get(
            url,
            {
                headers: {
                    "Accept": USER_CONTENT_TYPE
                }
            }
        );
        return User.fromJSON(response.data);
    };

    const getAddresses = async (url) => {
        const response = await api.get(
            url,
            {
                headers: {
                    "Accept": USER_ADDRESS_CONTENT_TYPE
                }
            }
        );
        const ans = Array.isArray(response.data) ? response.data.map(data => Address.fromJSON(data)) : [];
        return ans;
    };

    const getReviews = async (url, query) => {
        const response = await api.get(
            url,
            {
                params: query,
                headers: {
                    "Accept": REVIEW_CONTENT_TYPE
                }
            }
        );
        const links = parseLinkHeader(response.headers?.link, {});
        const reviews = Array.isArray(response.data) ? response.data.map(data => Review.fromJSON(data)) : [];
        return new PagedContent(
            reviews,
            links?.first,
            links?.prev,
            links?.next,
            links?.last
        );
    };

    const getRoleForRestaurant = async (url) => {
        try {
            const response = await api.get(
                url,
                {
                    headers: {
                        "Accept": RESTAURANT_EMPLOYEES_CONTENT_TYPE
                    }
                }
            );
            return UserRoleForRestaurant.fromJSON(response.data);
        } catch (e) {
            if (e.response.status === NOT_FOUND_STATUS_CODE) {
                return UserRoleForRestaurant.notEmployee();
            }
            throw e;
        }
    };

    const registerAddress = async (url, name, address) => {
        return await api.post(
            url,
            {
                name: name,
                address: address
            },
            {
                headers: {
                    "Content-Type": USER_ADDRESS_CONTENT_TYPE
                }
            }
        );
    };

    const deleteAddress = async (url) => {
        return await api.delete(
            url
        );
    };

    return {
        sendResetPasswordToken,
        resetPassword,
        login,
        register,
        getUser,
        getAddresses,
        getReviews,
        getRoleForRestaurant,
        registerAddress,
        deleteAddress
    };
}
