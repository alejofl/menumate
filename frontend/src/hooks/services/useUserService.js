import {
    NOT_FOUND_STATUS_CODE,
    RESTAURANT_EMPLOYEES_CONTENT_TYPE,
    ROLES,
    USER_ADDRESS_CONTENT_TYPE,
    USER_CONTENT_TYPE,
    USER_PASSWORD_CONTENT_TYPE,
    USER_ROLE_CONTENT_TYPE
} from "../../utils.js";
import User from "../../data/model/User.js";
import Address from "../../data/model/Address.js";
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
        return Array.isArray(response.data) ? response.data.map(data => Address.fromJSON(data)) : [];
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

    const getUsers = async (url, params) => {
        const response = await api.get(
            url,
            {
                params: params,
                headers: {
                    "Accept": USER_ROLE_CONTENT_TYPE
                }
            }
        );
        return Array.isArray(response.data) ? response.data.map(data => User.fromJSON(data)) : [];
    };

    const addModerator = async (url, email) => {
        return await api.post(
            url,
            {
                email: email,
                role: ROLES.MODERATOR
            },
            {
                headers: {
                    "Content-Type": USER_ROLE_CONTENT_TYPE
                }
            }
        );
    };

    const deleteAddress = async (url) => {
        return await api.delete(
            url
        );
    };

    const updateAddress = async (url, name, address) => {
        return await api.patch(
            url,
            {
                name,
                address
            },
            {
                headers: {
                    "Content-Type": USER_ADDRESS_CONTENT_TYPE
                }
            }
        );
    };

    const deleteModerator = async (url) => {
        return await api.delete(url);
    };

    return {
        sendResetPasswordToken,
        resetPassword,
        login,
        register,
        getUser,
        getAddresses,
        getRoleForRestaurant,
        getUsers,
        addModerator,
        deleteModerator,
        registerAddress,
        deleteAddress,
        updateAddress
    };
}
