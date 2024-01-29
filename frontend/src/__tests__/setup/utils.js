export const apiUrl = (path) => {
    if (path.startsWith(import.meta.env.VITE_API_BASE_URL)) {
        return path;
    }
    return `${import.meta.env.VITE_API_BASE_URL}${path}`;
};
