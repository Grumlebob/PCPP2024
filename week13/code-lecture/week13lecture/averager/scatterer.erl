% raup@itu.dk * 2024-11-21
-module(scatterer).
-export([start/1, init/1]).

% State
-record(scatterer_state, {gatherer}).

% Start
start(GathererPID) ->
    spawn(?MODULE, init, [GathererPID]).

% Initialization
init(GathererPID) ->
    State = #scatterer_state{gatherer=GathererPID},
    no_loop(State).

% Behavior upon receiving messages
% NOTE: This time the function is not a loop, as scatterer simply
% scatter if necessary or send a message to the gatherer (see message
% handling functions)
no_loop(State) ->
    receive
        {scatter, List, ListSize} ->
            handle_scatter(List, ListSize, State)
    end.

% Message handlers
handle_scatter(List, ListSize,
               #scatterer_state{gatherer = GathererPID}) 
  when length(List) > 1 -> 
    ListLength = length(List),
    Pivot = ListLength div 2, 

    LeftSubList  = lists:sublist(List, Pivot), 
    RightSubList = lists:sublist(List, Pivot+1, ListLength),

    Gatherer = gatherer:start(GathererPID),

    ScattererLeft = scatterer:start(Gatherer),
    ScattererRight = scatterer:start(Gatherer),

    ScattererLeft ! {scatter, LeftSubList, ListSize},
    ScattererRight ! {scatter, RightSubList, ListSize};

handle_scatter([H|[]], ListSize, #scatterer_state{gatherer = GathererPID}) -> 
    GathererPID ! {gather, H/ListSize};

handle_scatter([], _ListSize, #scatterer_state{gatherer = GathererPID}) -> 
    GathererPID ! {gather, 0}.
