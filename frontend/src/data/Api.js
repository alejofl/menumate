import axios from "axios";
import Qs from "qs";

export default axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    paramsSerializer: (params) => Qs.stringify(params, { arrayFormat: "repeat" })
});
