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

    return {
        sendResetPasswordToken,
        resendVerificationToken
    };
}
