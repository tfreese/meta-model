/**
 * Created: 22.07.2018
 */

package de.freese.metamodel.modelgen.naming;

/**
 * Basis-Implementierung der Namenskonvertierung.
 *
 * @author Thomas Freese
 */
public abstract class AbstractNamingStrategy implements NamingStrategy
{
    /**
     * Erstellt ein neues {@link AbstractNamingStrategy} Object.
     */
    protected AbstractNamingStrategy()
    {
        super();
    }

    /**
     * Ersetzt folgende Zeichen durch Leerzeichen:<br>
     * - \r<br>
     * - \n<br>
     * und führt ein trim und toLowerCase durch.
     *
     * @param value String
     * @return String
     */
    protected String normalize(final String value)
    {
        if (value != null)
        {
            return value.trim().replace('\r', ' ').replace('\n', ' ').toLowerCase();
        }

        return null;
    }

    /**
     * @param value String
     * @return String
     */
    protected String toCamelCase(final String value)
    {
        if (value == null)
        {
            return null;
        }

        String str = value.trim();

        if (str.length() == 0)
        {
            return "";
        }

        StringBuilder builder = new StringBuilder(str.length());

        for (int i = 0; i < str.length(); i++)
        {
            if (i == 0)
            {
                builder.append(Character.toUpperCase(str.charAt(i)));
            }
            else if ((i < (str.length() - 1)) && ((str.charAt(i) == '_') || (str.charAt(i) == '-') || (str.charAt(i) == ' ')))
            {
                i += 1;

                if (i < str.length())
                {
                    builder.append(Character.toUpperCase(str.charAt(i)));
                }
            }
            else
            {
                builder.append(str.charAt(i));
            }
        }

        return builder.toString();
    }
}
