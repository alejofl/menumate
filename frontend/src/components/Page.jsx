import Footer from "./Footer.jsx";
import Navbar from "./Navbar.jsx";
import { useTitle } from "../hooks/useTitle.js";

function Page({title, showNavbar = true, showFooter = true, children}) {
    useTitle(title);

    return (
        <>
            {showNavbar && <Navbar/>}
            <main>
                {children}
            </main>
            {showFooter && <Footer/>}
        </>
    );
}

export default Page;
