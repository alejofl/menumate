import Footer from "./Footer.jsx";
import Navbar from "./Navbar.jsx";

function Page({children}) {
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
