import Footer from "./Footer.jsx";
import Navbar from "./Navbar.jsx";
import { useTitle } from "../utils/useTitle.js";

function Page({title, children}) {
    useTitle(title);

    return (
        <>
            <Navbar/>
            <main>
                {children}
            </main>
            <Footer/>
        </>
    );
}

export default Page;
