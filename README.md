This app uses **llama3.2** as the LLM and **mxbai-embed-large** as the embedding model

There are limtations to LLM's where we can have stale data since the LLM's models are trained at a certain point in time.
As time passes by, it must be trained again so it can have the proper data in order to return **accurate** information.

Query 1 -> LLM (**OpenAI, PaLM2, llama**) -> Output Result

To address the issue above we use something called **Retrieval Augmented Generation (RAG)**.

- RAG will be the source of information for what context that you want to provide.
- For example, in sports things are constantly changes (ie, Sports team record, player trade, teams likely hood of making 
  it to the playoffs).
  - For this we can have a RAG model (**our source of truth)** which stores the current state of the league (ie, team record, player info
  - This information is then passed to the LLM which process the query using the context retrieved from the RAG, which results in the more 
    accurate responses

Benefits: 
- Single Source of truth so we get a more accurate response
- More control over the data  
- Saves the need for traning the LLM again which is costly