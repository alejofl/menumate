import { useTranslation } from "react-i18next";
import Page from "../components/Page.jsx";
import "./styles/myprofile.styles.css";
import {useApi} from "../hooks/useApi.js";
import {useContext} from "react";
import {useUserService} from "../hooks/services/useUserService.js";
import {useQuery} from "@tanstack/react-query";
import AuthContext from "../contexts/AuthContext.jsx";


function MyProfile() {
    const { t } = useTranslation();
    const api = useApi();
    const authContext = useContext(AuthContext);
    const userService = useUserService(api);

    const { data : User } = useQuery({
        queryKey: ["user", "myProfile"],
        queryFn: async () => (
            await userService.getUser(
                authContext.selfUrl
            )
        )
    });

    const {
        isPending,
        data: addresses
    } = useQuery({
        queryKey: ["addresses"],
        queryFn: async () => (
            await userService.getAddresses(
                User.addressesUrl
            )
        ),
        enabled: !!User
    });

    const {
        data: reviews
    } = useQuery( {
        queryKey: ["reviews"],
        queryFn: async () => (
            await userService.getReviews(
                User.reviewsUrl
            )
        ),
        enabled: !!User
    });
    console.log(reviews);

    return (
        <>
            <Page title={t("titles.myprofile")} className="myprofile">
                <div className="profile-container">
                    <div className="user-info-container">
                        <div className="card">
                            <div className="card-body">
                                <h3 className="card-title">{t("myprofile.profile")}</h3>
                                <label htmlFor="exampleFormControlInput1" className="form-label">{t("myprofile.name_label")}</label>
                                <input type="email" className="form-control" id="exampleFormControlInput1" disabled value={User?.name}/>
                                <label htmlFor="exampleFormControlInput1" className="form-label">{t("myprofile.email_label")}</label>
                                <input type="email" className="form-control" id="exampleFormControlInput1" value={User?.email} disabled/>
                                <hr/>
                                <h3 className="card-title">{t("myprofile.addresses")}</h3>
                                {!isPending && addresses.map(address => (
                                    // eslint-disable-next-line react/jsx-key
                                    <li className="list-group-item d-flex align-items-center justify-content-between px-0 address-list">
                                        <div className="d-flex align-items-center ">
                                            <i className="bi bi-geo-alt"></i>
                                        </div>
                                        <div className="d-flex gap-3">{address.address}</div>
                                    </li>
                                ))}
                            </div>
                        </div>
                    </div>
                    <div className="reviews-container">
                        <h3 className="page-title">{t("myprofile.reviews")}</h3>

                        <div className="card">
                            <div className="card-header">
                                <div className="my-review-card-header">
                                    <div className="my-review-card-header-info">
                                        {/* <b><c:out value="${review.order.restaurant.name}"/></b>*/}
                                        {/* <fmt:parseDate value="${review.date}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedDateOrdered" type="both"/>*/}
                                        {/* <fmt:formatDate pattern="dd MMMM yyyy - HH:mm" value="${parsedDateOrdered}" var="reviewDate"/>*/}
                                        {/* <small className="text-muted">${reviewDate}</small>*/}
                                    </div>
                                    <div className="d-flex gap-2 align-items-baseline mb-2 ">
                                        <div className="small-ratings">
                                            {/* <c:forEach begin="1" end="3">*/}
                                            {/*    <i className="bi bi-star-fill rating-color"></i>*/}
                                            {/* </c:forEach>*/}
                                            {/* <c:forEach begin="1" end="5 - 3">*/}
                                            {/*    <i className="bi bi-star-fill"></i>*/}
                                            {/* </c:forEach>*/}
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div className="card-body">
                                <p>Testing</p>
                            </div>
                        </div>
                    </div>
                </div>
            </Page>
        </>
    );
}

export default MyProfile;
