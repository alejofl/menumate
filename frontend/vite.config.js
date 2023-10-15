import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react-swc'

// https://vitejs.dev/config/
export default defineConfig(({ command, mode }) => {
  return {
    base: mode === "production" ? "/paw-2023a-01" : "/",
    plugins: [react()],
  }
})
