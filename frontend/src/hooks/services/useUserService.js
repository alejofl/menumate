export function useUserService(api) {
    const sendResetPasswordToken = async (url, email) => {
        return await api.post(url, {
            email: email
        });
    };

    const resendVerificationToken = async (url, email) => {
        return await api.post(url, {
            email: email
        });
    };

    const resetPassword = async (url, newPassword) => {
        return await api.put(url, {
            password: newPassword
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

    const register = async (url, name, email, password) => {
        return await api.post(url, {
            name: name,
            email: email,
            password: password
        });
    };

    return {
        sendResetPasswordToken,
        resendVerificationToken,
        resetPassword,
        login,
        register
    };
}
