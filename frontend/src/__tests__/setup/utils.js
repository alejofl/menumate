export const apiUrl = (path) => {
    if (path.startsWith(import.meta.env.VITE_API_BASE_URL)) {
        return path;
    }
    return `${import.meta.env.VITE_API_BASE_URL}${path}`;
};

export const DUMMY_AUTH_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJ0b2tlblR5cGUiOiJNT0NLLUFDQ0VTUyIsIm5hbWUiOiJKb2huIERvZSIsInJvbGUiOiJNT0RFUkFUT1IiLCJzZWxmVXJsIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3Bhdy0yMDIzYS0wMS9hcGkvdXNlcnMvMSIsInN1YiI6ImFsZWpvQG1pc3RlcmZsb3Jlcy5jb20iLCJpYXQiOjE3MDY3MTk1MzMsImV4cCI6MTcwNjcyMDEzM30.lj53LaO6g6oGfY0dz9pFC31kZpnvMSUAsmWtgV1MA4AuWiTh3ykduVY52ljJXCR2Acgb3BjgAa1PhhvaH5OBmA";
export const DUMMY_REFRESH_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJ0b2tlblR5cGUiOiJNT0NLIFJFRlJFU0giLCJzdWIiOiJhbGVqb0BtaXN0ZXJmbG9yZXMuY29tIiwiaWF0IjoxNzA2NzE5NTMzLCJleHAiOjE3MDczMjQzMzN9.FUcyHdDNSm3brBW-QVPuA45nHVACPzQ6Q46gzAk7Z82bIhIFhxKkZe27nVcMaJSxlQWufZpUtx5a8ydoMUu16A";
