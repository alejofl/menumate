import axios from "axios";

export default axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    timeout: 5000,
    // eslint-disable-next-line no-undef
    paramsSerializer: (params) => Qs.stringify(params, { arrayFormat: "repeat" })
});
