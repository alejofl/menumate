import { defineConfig } from "vite";
import react from "@vitejs/plugin-react-swc";

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
    return {
        base: mode === "production" ? "/paw-2023a-01" : "/",
        plugins: [react()],
        test: {
            environment: "jsdom",
            setupFiles: ["./src/__tests__/setup/setup.js"]
        }
    };
});
