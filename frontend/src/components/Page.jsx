import Footer from "./Footer.jsx";
import Navbar from "./Navbar.jsx";
import { useTitle } from "../hooks/useTitle.js";

function Page({title, className, showNavbar = true, showFooter = true, children}) {
    useTitle(title);

    return (
        <>
            {showNavbar && <Navbar/>}
            <main className={className}>
                {children}
            </main>
            {showFooter && <Footer/>}
        </>
    );
}

export default Page;
