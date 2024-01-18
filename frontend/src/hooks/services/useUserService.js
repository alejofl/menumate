import {USER_ADDRESS_CONTENT_TYPE, USER_CONTENT_TYPE, USER_PASSWORD_CONTENT_TYPE} from "../../utils.js";
import User from "../../data/model/User.js";
import Address from "../../data/model/Address.js";

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
        return Array.isArray(response.data) ? response.data.map(data => Address.fromJSON(data)) : [];
    };

    return {
        sendResetPasswordToken,
        resetPassword,
        login,
        register,
        getUser,
        getAddresses
    };
}
