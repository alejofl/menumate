import { useEffect } from "react";
import { useTranslation } from "react-i18next";
import {useInfiniteQuery} from "@tanstack/react-query";
import {useApi} from "../hooks/useApi.js";
import {userReviewService} from "../hooks/services/useReviewService.js";
import ContentLoader from "react-content-loader";
import Rating from "./Rating.jsx";

function ReviewsModal({reviewsUrl, onClose}) {
    const { t, i18n } = useTranslation();
    const api = useApi();
    const reviewService = userReviewService(api);

    useEffect(() => {
        // eslint-disable-next-line no-undef
        const modal = new bootstrap.Modal(document.querySelector(".reviews_modal .modal"));
        modal.show();

        document.addEventListener("hidden.bs.modal", () => onClose());

        return () => {
            document.removeEventListener("hidden.bs.modal", null);
        };
    }, [onClose]);

    const {
        isPending,
        isError,
        data,
        isFetchingNextPage,
        hasNextPage,
        fetchNextPage
    } = useInfiniteQuery({
        queryKey: ["reviews", reviewsUrl],
        queryFn: async ({ pageParam }) => (
            await reviewService.getReviews(
                reviewsUrl,
                {
                    page: pageParam
                }
            )
        ),
        getNextPageParam: (lastPage) => lastPage.nextPage?.page || undefined,
        keepPreviousData: true
    });

    const handleLoadMoreContent = async () => {
        await fetchNextPage();
    };

    return (
        <div className="reviews_modal">
            <div className="modal fade" tabIndex="-1">
                <div className="modal-dialog modal-dialog-centered modal-dialog-scrollable">
                    <div className="modal-content">
                        <div className="modal-header">
                            <h1 className="modal-title fs-5">{t("restaurant.reviews.title")}</h1>
                        </div>
                        <div className="modal-body">
                            {isError || isPending
                                ?
                                <ContentLoader backgroundColor="#eaeaea" foregroundColor="#e0e0e0" width="466">
                                    <rect x="0" y="0" rx="0" ry="0" width="466" height="300"/>
                                    <rect x="324" y="0" rx="0" ry="0" width="466" height="380"/>
                                    <rect x="648" y="0" rx="0" ry="0" width="466" height="380"/>
                                </ContentLoader>
                                :
                                <>
                                    <div className="list-group list-group-flush">
                                        {data.pages.flatMap(page => page.content).map((review, i) => (
                                            <div className="list-group-item mt-3" key={i}>
                                                <b>{review.reviewerName}</b>
                                                <div className="d-flex gap-2 align-items-baseline my-2">
                                                    <Rating rating={review.rating}/>
                                                    <small className="text-muted">{review.date.toLocaleString(i18n.language)}</small>
                                                </div>
                                                <p className="mb-4">
                                                    {review.comment}
                                                </p>
                                                {review.reply &&
                                                    <div className="alert alert-light mt-2" role="alert">
                                                        <b>{t("restaurant.reviews.reply")}</b>
                                                        <p className="m-0">
                                                            {review.reply}
                                                        </p>
                                                    </div>
                                                }
                                            </div>
                                        ))}
                                        {isFetchingNextPage &&
                                            <ContentLoader backgroundColor="#eaeaea" foregroundColor="#e0e0e0" width="466">
                                                <rect x="0" y="0" rx="0" ry="0" width="466" height="300"/>
                                                <rect x="324" y="0" rx="0" ry="0" width="466" height="380"/>
                                                <rect x="648" y="0" rx="0" ry="0" width="466" height="380"/>
                                            </ContentLoader>
                                        }
                                    </div>
                                    {hasNextPage &&
                                        <div className="d-flex justify-content-center align-items-center pt-2 pb-3">
                                            <button onClick={handleLoadMoreContent} className="btn btn-primary" disabled={isFetchingNextPage}>
                                                {
                                                    isFetchingNextPage
                                                        ?
                                                        <>
                                                            <span className="spinner-border spinner-border-sm mx-4" role="status"></span>
                                                            <span className="visually-hidden">Loading...</span>
                                                        </>
                                                        :
                                                        t("paging.load_more")
                                                }
                                            </button>
                                        </div>
                                    }
                                </>
                            }
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default ReviewsModal;
