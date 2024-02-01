export default class PagedContent {
    constructor(
        content,
        firstPage,
        prevPage,
        nextPage,
        lastPage
    ) {
        this.content = content;
        this.firstPage = firstPage;
        this.prevPage = prevPage;
        this.nextPage = nextPage;
        this.lastPage = lastPage;
    }
}
