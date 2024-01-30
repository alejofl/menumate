import { setupServer } from "msw/node";
import { beforeAll, beforeEach, afterEach, afterAll } from "vitest";
import { ordersHandlers } from "../mocks/OrdersApiMock.js";
import {restaurantsHandlers} from "../mocks/RestaurantsApiMock.js";
import {imagesHandlers} from "../mocks/ImagesApiMock.js";

const server = setupServer(...[
    ...ordersHandlers,
    ...restaurantsHandlers,
    ...imagesHandlers
]);

// Enable request interception.
beforeAll(() => server.listen());

beforeEach( (context) => context.server = server);

afterEach(() => server.resetHandlers());

afterAll(() => server.close());
