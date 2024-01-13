import {RESET_PASSWORD_CONTENT_TYPE} from "../../utils.js";

export function useUserService(api) {
    const sendResetPasswordToken = async (url, email) => {
        return await api.post(
            url,
            {
                email: email
            },
            {
                headers: {
                    "Content-Type": RESET_PASSWORD_CONTENT_TYPE
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
                    "Content-Type": RESET_PASSWORD_CONTENT_TYPE
                }
            });
    };

    const login = async (url, email, password) => {
        const response = await api.get(url, {
            headers: {
                "Authorization": `Basic ${btoa(`${email}:${password}`)}`
            }
        });
        if (response.headers["x-menumate-authtoken"]) {
            return {success: true, jwt: response.headers["x-menumate-authtoken"]};
        } else {
            return {success: false, jwt: null};
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
                    "Accept-Language": language
                }
            }
        );
    };

    return {
        sendResetPasswordToken,
        resetPassword,
        login,
        register
    };
}
