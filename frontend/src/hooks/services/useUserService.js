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

    return {
        sendResetPasswordToken,
        resendVerificationToken,
        resetPassword
    };
}
