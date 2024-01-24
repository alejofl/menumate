import {useEffect, useState} from "react";
import { useTranslation } from "react-i18next";
import {useInfiniteQuery, useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import {useApi} from "../hooks/useApi.js";
import ContentLoader from "react-content-loader";
import Error from "../pages/Error.jsx";
import {useReportService} from "../hooks/services/useReportService.js";
import {useRestaurantService} from "../hooks/services/useRestaurantService.js";
import {useNavigate} from "react-router-dom";

function ReportsModal({restaurantUrl, restaurantId, restaurantName, reportsUrl, onClose}) {
    const { t, i18n } = useTranslation();
    const navigate = useNavigate();
    const api = useApi();
    const restaurantService = useRestaurantService(api);
    const reportService = useReportService(api);

    const [showErrorAlert, setShowErrorAlert] = useState(false);

    useEffect(() => {
        // eslint-disable-next-line no-undef
        const modal = bootstrap.Modal.getOrCreateInstance(document.querySelector(".reports_modal .modal"));
        modal.show();
    }, []);
    useEffect(() => {
        document.querySelector(".reports_modal .modal").addEventListener("hidden.bs.modal", () => {
            onClose();
        });
    }, []);

    const queryClient = useQueryClient();

    const {
        isPending: reportsIsPending,
        isError: reportsIsError,
        data: reports,
        error: reportsError,
        isFetchingNextPage,
        hasNextPage,
        fetchNextPage
    } = useInfiniteQuery({
        queryKey: ["restaurant", restaurantId, "reports"],
        queryFn: async ({ pageParam }) => (
            await reportService.getReports(
                reportsUrl,
                {
                    page: pageParam
                }
            )
        ),
        getNextPageParam: (lastPage) => lastPage.nextPage?.page || undefined,
        keepPreviousData: true
    });
    const { isPending: restaurantIsPending, isError: restaurantIsError, data: restaurant, error: restaurantError } = useQuery({
        queryKey: ["restaurant", restaurantId],
        queryFn: async () => (
            await restaurantService.getRestaurant(restaurantUrl, false)
        )
    });

    const markAsHandledMutation = useMutation({
        mutationFn: async (reportUrl) => (
            await reportService.markAsHandled(reportUrl)
        ),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: ["restaurant", restaurantId, "reports"]});
            queryClient.invalidateQueries({queryKey: ["moderators", "restaurants"]});
        },
        onError: () => setShowErrorAlert(true)
    });
    const toggleActiveMutation = useMutation({
        mutationFn: async (activate) => (
            await reportService.toggleActiveForRestaurant(restaurantUrl, activate)
        ),
        onSuccess: () => queryClient.invalidateQueries({queryKey: ["restaurant", restaurantId]}),
        onError: () => setShowErrorAlert(true)
    });
    const toggleDeleteMutation = useMutation({
        mutationFn: async (deleted) => (
            await reportService.toggleDeleteForRestaurant(restaurantUrl, deleted)
        ),
        onSuccess: () => queryClient.invalidateQueries({queryKey: ["restaurant", restaurantId]}),
        onError: () => setShowErrorAlert(true)
    });

    const handleLoadMoreContent = async () => {
        await fetchNextPage();
    };

    if (reportsIsError) {
        return (
            <Error error={reportsError.response.status}/>
        );
    } else if (restaurantIsError) {
        return (
            <Error error={restaurantError.response.status}/>
        );
    }
    return (
        <div className="reports_modal">
            <div className="modal fade" tabIndex="-1">
                <div className="modal-dialog modal-dialog-centered modal-dialog-scrollable">
                    <div className="modal-content">
                        <div className="modal-header flex-column align-items-start gap-2">
                            <h1 className="modal-title fs-5">{t("moderators_panel.reports.title", {restaurantName: restaurantName})}</h1>
                            {
                                !restaurantIsPending &&
                                <div className="d-flex gap-2 w-100">
                                    <button
                                        data-bs-dismiss="modal"
                                        onClick={() => {
                                            navigate(`/restaurants/${restaurantId}`);
                                        }}
                                        type="button"
                                        className="btn btn-secondary btn-sm flex-grow-1"
                                    >
                                        {t("moderators_panel.reports.view_restaurant")}
                                    </button>
                                    {
                                        !restaurant.deleted &&
                                        <button
                                            onClick={() => toggleActiveMutation.mutate(!restaurant.active)}
                                            type="button"
                                            className="btn btn-accent btn-sm flex-grow-1"
                                        >
                                            {
                                                restaurant.active
                                                    ?
                                                    t("moderators_panel.reports.deactivate_restaurant")
                                                    :
                                                    t("moderators_panel.reports.activate_restaurant")
                                            }
                                        </button>
                                    }
                                    <button
                                        onClick={() => toggleDeleteMutation.mutate(!restaurant.deleted)}
                                        type="button"
                                        className="btn btn-danger btn-sm flex-grow-1"
                                    >
                                        {
                                            restaurant.deleted
                                                ?
                                                t("moderators_panel.reports.restore_restaurant")
                                                :
                                                t("moderators_panel.reports.delete_restaurant")
                                        }
                                    </button>
                                </div>
                            }
                        </div>
                        <div className="modal-body">
                            {reportsIsPending || restaurantIsPending
                                ?
                                <ContentLoader backgroundColor="#eaeaea" foregroundColor="#e0e0e0" width="466">
                                    <rect x="0" y="0" rx="0" ry="0" width="466" height="300"/>
                                    <rect x="324" y="0" rx="0" ry="0" width="466" height="380"/>
                                    <rect x="648" y="0" rx="0" ry="0" width="466" height="380"/>
                                </ContentLoader>
                                :
                                <>
                                    {showErrorAlert && <div className="alert alert-danger" role="alert">{t("moderators_panel.reports.error_alert")}</div>}
                                    <div className="list-group list-group-flush">
                                        {reports.pages.flatMap(page => page.content).map((report, i) => (
                                            <div className="list-group-item mt-3" key={i}>
                                                <b>{t("moderators_panel.reports.anonymous_user")}</b>
                                                <div>
                                                    <small className="text-muted">{report.dateReported.toLocaleString(i18n.language)}</small>
                                                </div>
                                                <p className="my-2">
                                                    {report.comment}
                                                </p>
                                                <div className="mb-4 d-flex justify-content-end">
                                                    <button
                                                        type="button"
                                                        className={`btn btn-primary btn-sm ${report.handled ? "disabled" : ""}`}
                                                        disabled={report.handled}
                                                        onClick={() => markAsHandledMutation.mutate(report.selfUrl)}
                                                    >
                                                        {
                                                            report.handled
                                                                ?
                                                                t("moderators_panel.reports.already_handled")
                                                                :
                                                                t("moderators_panel.reports.mark_as_handled")
                                                        }
                                                    </button>
                                                </div>
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

export default ReportsModal;
