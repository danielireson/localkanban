# Localkanban

Private kanban board that saves to local storage.

![Screenshot 2021-06-26 at 15:46:11](https://user-images.githubusercontent.com/9462036/123516718-6e10d480-d695-11eb-81c7-f3f8789f7c44.png)

## Technology

Shadow CLJS, ClojureScript, Reagent.

## Motivation

I've been looking at Clojure recently and wanted to put together a small application in ClojureScript to evaluate the JS interop. This is a simple SPA that leverages Reagent for React components and state management.

## Development

```shell
# install dependencies
npm install

# serve locally
npm start
```

This starts the Shadow CLJS app on port 3000 and watches for changes.

## Production

```shell
# install dependencies
npm install

# compile for production
npm run build
```

This builds the client app to `/public` ready for deploy to a static site host. A Dockerfile has also been included for building and launching the app in container environments using an integrated Node.js server.

## Future

- Draggable cards
- Offline support

## License

This project is licensed under the MIT License.
