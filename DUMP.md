
- remove chunking responsibility from reader, reader should just call consumer
- decouple reader from writer, parallel reading and writing
- decouple transaction from chunks, have transaction spawn all chunks
- fail job without exception
- fail job but still commit
- job without steps
- xa? jms <-> db?

