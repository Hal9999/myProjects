package redcat.common.event;

import redcat.common.*;

/**
 * Realizza un filtro per oggetti ExtendedEvent secondo la classificazione a cui appartengono.
 * <p>Implementa i metodi della classe astratta FilterView.</p>
 * @see FilterView
 * @author Lanzafame Flavio, Russo Giorgia, Spadaro Stefano, Torrelli Denisia
 */
public class ExtendedEventTypeFilterView extends FilterView<ExtendedEvent>
{
    /**
     * Crea una View.
     * @param model il model di cui filtrare gli ExtendedEvent
     * @param type la classificazione degli eventi accettati da questa ExtendedEventTypeFilterView
     */
    public ExtendedEventTypeFilterView(Observable<ExtendedEvent> model, String type)
    {
        super(model, type);
    }

    @Override
    protected boolean isInteresting(ExtendedEvent obj)
    {
        return type.equals(obj.type);
    }
}