# Localkanban

Private kanban board that saves to local storage.

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

This builds the client app to `/web` ready for deploy to a static site host.

## Future

- Draggable cards
- Offline support

## License

This project is licensed under the MIT License.
