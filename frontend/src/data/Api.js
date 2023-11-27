import axios from "axios";
import Qs from "qs";

export default axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    timeout: 5000,

    paramsSerializer: (params) => Qs.stringify(params, { arrayFormat: "repeat" })
});
