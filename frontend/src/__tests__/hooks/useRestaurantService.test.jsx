/* eslint-disable no-magic-numbers */
import {describe, it, expect} from "vitest";
import Api from "../../data/Api.js";
import {apiUrl} from "../setup/utils.js";
import PagedContent from "../../data/model/PagedContent.js";
import {useRestaurantService} from "../../hooks/services/useRestaurantService.js";
import Restaurant from "../../data/model/Restaurant.js";
import Category from "../../data/model/Category.js";
import Product from "../../data/model/Product.js";
import Promotion from "../../data/model/Promotion.js";
import UserRoleForRestaurant from "../../data/model/UserRoleForRestaurant.js";

describe("useRestaurantService", () => {
    describe("getRestaurants", () => {
        it("should return an instance of PagedContent", async () => {
            const restaurantService = useRestaurantService(Api);
            const response = await restaurantService.getRestaurants(apiUrl("/restaurants"));
            expect(response).toBeInstanceOf(PagedContent);
        });

        it("should return an array of Restaurant as content", async () => {
            const restaurantService = useRestaurantService(Api);
            const response = await restaurantService.getRestaurants(apiUrl("/restaurants"));
            response.content.forEach((restaurant) => expect(restaurant).toBeInstanceOf(Restaurant));
        });

        it("should return correct data", async () => {
            const restaurantService = useRestaurantService(Api);
            const response = await restaurantService.getRestaurants(apiUrl("/restaurants"));
            expect(response.content[0].active).toEqual(expect.any(Boolean));
            expect(response.content[0].address).toEqual(expect.any(String));
            expect(response.content[0].categoriesUrl).toEqual(expect.any(String));
            expect(response.content[0].dateCreated).toEqual(expect.anything());
            expect(response.content[0].deleted).toEqual(expect.any(Boolean));
            expect(response.content[0].description).toEqual(expect.any(String));
            expect(response.content[0].employeesUriTemplate).toEqual(expect.anything());
            expect(response.content[0].logoUrl).toEqual(expect.anything());
            expect(response.content[0].maxTables).toEqual(expect.any(Number));
            expect(response.content[0].name).toEqual(expect.any(String));
            expect(response.content[0].ordersUrl).toEqual(expect.any(String));
            expect(response.content[0].ownerUrl).toEqual(expect.any(String));
            expect(response.content[0].portrait1Url).toEqual(expect.anything());
            expect(response.content[0].portrait2Url).toEqual(expect.anything());
            expect(response.content[0].promotionsUrl).toEqual(expect.any(String));
            expect(response.content[0].reportsUrl).toEqual(expect.any(String));
            expect(response.content[0].restaurantId).toEqual(expect.any(Number));
            expect(response.content[0].reviewsUrl).toEqual(expect.any(String));
            expect(response.content[0].selfUrl).toEqual(expect.any(String));
            expect(response.content[0].specialty).toEqual(expect.any(String));
            expect(response.content[0].tags).toEqual(expect.any(Array));
            expect(response.content[0].averageProductPrice).toEqual(expect.any(Number));
            expect(response.content[0].averageRating).toEqual(expect.any(Number));
            expect(response.content[0].reviewCount).toEqual(expect.anything());
        });
    });

    describe("getRestaurant", () => {
        it("should return an instance of Restaurant", async () => {
            const restaurantService = useRestaurantService(Api);
            const response = await restaurantService.getRestaurant(apiUrl("/restaurants/1"), true);
            expect(response).toBeInstanceOf(Restaurant);
        });
    });

    describe("getCategories", () => {
        it("should return an array", async () => {
            const restaurantService = useRestaurantService(Api);
            const response = await restaurantService.getCategories(apiUrl("/restaurants/1/categories"));
            expect(Array.isArray(response)).toBe(true);
        });

        it("should return an array of Category", async () => {
            const restaurantService = useRestaurantService(Api);
            const response = await restaurantService.getCategories(apiUrl("/restaurants/1/categories"));
            response.forEach((item) => expect(item).toBeInstanceOf(Category));
        });

        it("should return correct data", async () => {
            const restaurantService = useRestaurantService(Api);
            const response = await restaurantService.getCategories(apiUrl("/restaurants/1/categories"));
            expect(response[0].categoryId).toEqual(expect.any(Number));
            expect(response[0].deleted).toEqual(expect.any(Boolean));
            expect(response[0].name).toEqual(expect.any(String));
            expect(response[0].orderNum).toEqual(expect.any(Number));
            expect(response[0].productsUrl).toEqual(expect.any(String));
            expect(response[0].restaurantUrl).toEqual(expect.any(String));
            expect(response[0].selfUrl).toEqual(expect.any(String));
        });
    });

    describe("getProducts", () => {
        it("should return an array", async () => {
            const restaurantService = useRestaurantService(Api);
            const response = await restaurantService.getProducts(apiUrl("/restaurants/1/categories/1/products"));
            expect(Array.isArray(response)).toBe(true);
        });

        it("should return an array of Product", async () => {
            const restaurantService = useRestaurantService(Api);
            const response = await restaurantService.getProducts(apiUrl("/restaurants/1/categories/1/products"));
            response.forEach((item) => expect(item).toBeInstanceOf(Product));
        });

        it("should return correct data", async () => {
            const restaurantService = useRestaurantService(Api);
            const response = await restaurantService.getProducts(apiUrl("/restaurants/1/categories/1/products"));
            expect(response[0].available).toEqual(expect.any(Boolean));
            expect(response[0].categoryUrl).toEqual(expect.any(String));
            expect(response[0].deleted).toEqual(expect.any(Boolean));
            expect(response[0].name).toEqual(expect.any(String));
            expect(response[0].price).toEqual(expect.any(Number));
            expect(response[0].productId).toEqual(expect.any(Number));
            expect(response[0].selfUrl).toEqual(expect.any(String));
        });
    });

    describe("getPromotions", () => {
        it("should return an array", async () => {
            const restaurantService = useRestaurantService(Api);
            const response = await restaurantService.getPromotions(apiUrl("/restaurants/1/promotions"));
            expect(Array.isArray(response)).toBe(true);
        });

        it("should return an array of Promotion", async () => {
            const restaurantService = useRestaurantService(Api);
            const response = await restaurantService.getPromotions(apiUrl("/restaurants/1/promotions"));
            response.forEach((item) => expect(item).toBeInstanceOf(Promotion));
        });

        it("should return correct data", async () => {
            const restaurantService = useRestaurantService(Api);
            const response = await restaurantService.getPromotions(apiUrl("/restaurants/1/promotions"), false);
            expect(response[0].destinationUrl).toEqual(expect.any(String));
            expect(response[0].discountPercentage).toEqual(expect.any(Number));
            expect(response[0].selfUrl).toEqual(expect.any(String));
            expect(response[0].sourceUrl).toEqual(expect.any(String));
        });
    });

    describe("getProduct", () => {
        it("should return an instance of Product", async () => {
            const restaurantService = useRestaurantService(Api);
            const response = await restaurantService.getProduct(apiUrl("/restaurants/1/categories/1/products/1"));
            expect(response).toBeInstanceOf(Product);
        });
    });

    describe("reportRestaurant", () => {
        it("should return 201 Created", async () => {
            const restaurantService = useRestaurantService(Api);
            const response = await restaurantService.reportRestaurant(apiUrl("/restaurants/1/reports"), {});
            expect(response.status).toBe(201);
        });
    });

    describe("createRestaurant", () => {
        it("should return the new Restaurant ID", async () => {
            const restaurantService = useRestaurantService(Api);
            const response = await restaurantService.createRestaurant(apiUrl("/restaurants"), apiUrl("/images"), "", "", "", "", "", "", [], [], []);
            expect(response).toEqual(expect.any(Number));
        });
    });

    describe("getRestaurantsWithUnhandledReports", () => {
        it("should return an instance of PagedContent", async () => {
            const restaurantService = useRestaurantService(Api);
            const response = await restaurantService.getRestaurantsWithUnhandledReports(apiUrl("/restaurants"));
            expect(response).toBeInstanceOf(PagedContent);
        });

        it("should return an array of Restaurant as content", async () => {
            const restaurantService = useRestaurantService(Api);
            const response = await restaurantService.getRestaurantsWithUnhandledReports(apiUrl("/restaurants"));
            response.content.forEach((restaurant) => expect(restaurant).toBeInstanceOf(Restaurant));
        });
    });

    describe("addCategory", () => {
        it("should return 201 Created", async () => {
            const restaurantService = useRestaurantService(Api);
            const response = await restaurantService.addCategory(apiUrl("/restaurants/1/categories"), "");
            expect(response.status).toBe(201);
        });
    });

    describe("addProduct", () => {
        it("should return 201 Created", async () => {
            const restaurantService = useRestaurantService(Api);
            const response = await restaurantService.addProduct(apiUrl("/restaurants/1/categories/1/products"), apiUrl("/images"), "", "", 1, []);
            expect(response.status).toBe(201);
        });
    });

    describe("deleteRestaurant", () => {
        it("should return 204 No Content", async () => {
            const restaurantService = useRestaurantService(Api);
            const response = await restaurantService.deleteRestaurant(apiUrl("/restaurants/1"));
            expect(response.status).toBe(204);
        });
    });

    describe("editRestaurantInformation", () => {
        it("should return 204 No Content", async () => {
            const restaurantService = useRestaurantService(Api);
            const response = await restaurantService.editRestaurantInformation(apiUrl("/restaurants/1"), apiUrl("/images"), "", "", "", "", "", "", [], [], []);
            expect(response.status).toBe(204);
        });
    });

    describe("editCategory", () => {
        it("should return 204 No Content", async () => {
            const restaurantService = useRestaurantService(Api);
            const response = await restaurantService.editCategory(apiUrl("/restaurants/1/categories/1"), "");
            expect(response.status).toBe(204);
        });
    });

    describe("deleteCategory", () => {
        it("should return 204 No Content", async () => {
            const restaurantService = useRestaurantService(Api);
            const response = await restaurantService.deleteCategory(apiUrl("/restaurants/1/categories/1"));
            expect(response.status).toBe(204);
        });
    });

    describe("getEmployees", () => {
        it("should return an array", async () => {
            const restaurantService = useRestaurantService(Api);
            const response = await restaurantService.getEmployees(apiUrl("/restaurants/1/employees"));
            expect(Array.isArray(response)).toBe(true);
        });

        it("should return an array of UserRoleForRestaurant", async () => {
            const restaurantService = useRestaurantService(Api);
            const response = await restaurantService.getEmployees(apiUrl("/restaurants/1/employees"));
            response.forEach((item) => expect(item).toBeInstanceOf(UserRoleForRestaurant));
        });

        it("should return correct data", async () => {
            const restaurantService = useRestaurantService(Api);
            const response = await restaurantService.getEmployees(apiUrl("/restaurants/1/employees"));
            expect(response[0].email).toEqual(expect.any(String));
            expect(response[0].name).toEqual(expect.any(String));
            expect(response[0].restaurantUrl).toEqual(expect.any(String));
            expect(response[0].role).toEqual(expect.any(String));
            expect(response[0].selfUrl).toEqual(expect.any(String));
            expect(response[0].userUrl).toEqual(expect.any(String));
        });
    });

    describe("addEmployee", () => {
        it("should return 201 Created", async () => {
            const restaurantService = useRestaurantService(Api);
            const response = await restaurantService.addEmployee(apiUrl("/restaurants/1/employees"), "", "");
            expect(response.status).toBe(201);
        });
    });

    describe("deleteEmployee", () => {
        it("should return 204 No Content", async () => {
            const restaurantService = useRestaurantService(Api);
            const response = await restaurantService.deleteEmployee(apiUrl("/restaurants/1/employees/1"));
            expect(response.status).toBe(204);
        });
    });

    describe("editEmployeeRole", () => {
        it("should return 204 No Content", async () => {
            const restaurantService = useRestaurantService(Api);
            const response = await restaurantService.editEmployeeRole(apiUrl("/restaurants/1/employees/1"), "");
            expect(response.status).toBe(204);
        });
    });

    describe("updateCategoryOrder", () => {
        it("should return the new category order", async () => {
            const restaurantService = useRestaurantService(Api);
            const response = await restaurantService.updateCategoryOrder(apiUrl("/restaurants/1/categories/1"), 24);
            expect(response).toEqual(24);
        });
    });

    describe("deleteProduct", () => {
        it("should return 204 No Content", async () => {
            const restaurantService = useRestaurantService(Api);
            const response = await restaurantService.deleteProduct(apiUrl("/restaurants/1/categories/1/products/1"));
            expect(response.status).toBe(204);
        });
    });

    describe("deletePromotion", () => {
        it("should return 204 No Content", async () => {
            const restaurantService = useRestaurantService(Api);
            const response = await restaurantService.deletePromotion(apiUrl("/restaurants/1/promotions/1"));
            expect(response.status).toBe(204);
        });
    });

    describe("createPromotion", () => {
        it("should return 201 Created", async () => {
            const restaurantService = useRestaurantService(Api);
            const response = await restaurantService.createPromotion(apiUrl("/restaurants/1/promotions"), {});
            expect(response.status).toBe(201);
        });
    });

    describe("editProduct", () => {
        it("should return the new Category ID", async () => {
            const restaurantService = useRestaurantService(Api);
            const response = await restaurantService.editProduct(apiUrl("/restaurants/1/categories/1/products/1"), apiUrl("/images"), "", "", 1, 235, []);
            expect(response).toEqual(235);
        });
    });
});
