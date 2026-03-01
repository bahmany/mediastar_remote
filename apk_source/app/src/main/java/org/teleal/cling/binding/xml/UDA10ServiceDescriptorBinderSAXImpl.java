package org.teleal.cling.binding.xml;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.teleal.cling.binding.staging.MutableAction;
import org.teleal.cling.binding.staging.MutableActionArgument;
import org.teleal.cling.binding.staging.MutableAllowedValueRange;
import org.teleal.cling.binding.staging.MutableService;
import org.teleal.cling.binding.staging.MutableStateVariable;
import org.teleal.cling.binding.xml.Descriptor;
import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.meta.ActionArgument;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.meta.StateVariableEventDetails;
import org.teleal.cling.model.types.CustomDatatype;
import org.teleal.cling.model.types.Datatype;
import org.teleal.common.xml.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/* loaded from: classes.dex */
public class UDA10ServiceDescriptorBinderSAXImpl extends UDA10ServiceDescriptorBinderImpl {
    private static Logger log = Logger.getLogger(ServiceDescriptorBinder.class.getName());

    @Override // org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderImpl, org.teleal.cling.binding.xml.ServiceDescriptorBinder
    public <S extends Service> S describe(S s, String str) throws ValidationException, DescriptorBindingException {
        if (str == null || str.length() == 0) {
            throw new DescriptorBindingException("Null or empty descriptor");
        }
        try {
            log.fine("Reading service from XML descriptor");
            SAXParser sAXParser = new SAXParser();
            MutableService mutableService = new MutableService();
            hydrateBasic(mutableService, s);
            new RootHandler(mutableService, sAXParser);
            sAXParser.parse(new InputSource(new StringReader(str.trim())));
            return (S) mutableService.build(s.getDevice());
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e2) {
            throw new DescriptorBindingException("Could not parse service descriptor: " + e2.toString(), e2);
        }
    }

    protected static class RootHandler extends ServiceDescriptorHandler<MutableService> {
        public RootHandler(MutableService instance, SAXParser parser) {
            super(instance, parser);
        }

        @Override // org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl.ServiceDescriptorHandler
        public void startElement(Descriptor.Service.ELEMENT element, Attributes attributes) throws SAXException {
            if (element.equals(ActionListHandler.EL)) {
                List<MutableAction> actions = new ArrayList<>();
                ((MutableService) getInstance()).actions = actions;
                new ActionListHandler(actions, this);
            }
            if (element.equals(StateVariableListHandler.EL)) {
                List<MutableStateVariable> stateVariables = new ArrayList<>();
                ((MutableService) getInstance()).stateVariables = stateVariables;
                new StateVariableListHandler(stateVariables, this);
            }
        }
    }

    protected static class ActionListHandler extends ServiceDescriptorHandler<List<MutableAction>> {
        public static final Descriptor.Service.ELEMENT EL = Descriptor.Service.ELEMENT.actionList;

        public ActionListHandler(List<MutableAction> instance, ServiceDescriptorHandler parent) {
            super(instance, parent);
        }

        @Override // org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl.ServiceDescriptorHandler
        public void startElement(Descriptor.Service.ELEMENT element, Attributes attributes) throws SAXException {
            if (element.equals(ActionHandler.EL)) {
                MutableAction action = new MutableAction();
                ((List) getInstance()).add(action);
                new ActionHandler(action, this);
            }
        }

        @Override // org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl.ServiceDescriptorHandler
        public boolean isLastElement(Descriptor.Service.ELEMENT element) {
            return element.equals(EL);
        }
    }

    protected static class ActionHandler extends ServiceDescriptorHandler<MutableAction> {
        private static /* synthetic */ int[] $SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Service$ELEMENT;
        public static final Descriptor.Service.ELEMENT EL = Descriptor.Service.ELEMENT.action;

        static /* synthetic */ int[] $SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Service$ELEMENT() {
            int[] iArr = $SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Service$ELEMENT;
            if (iArr == null) {
                iArr = new int[Descriptor.Service.ELEMENT.valuesCustom().length];
                try {
                    iArr[Descriptor.Service.ELEMENT.action.ordinal()] = 6;
                } catch (NoSuchFieldError e) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.actionList.ordinal()] = 5;
                } catch (NoSuchFieldError e2) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.allowedValue.ordinal()] = 18;
                } catch (NoSuchFieldError e3) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.allowedValueList.ordinal()] = 17;
                } catch (NoSuchFieldError e4) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.allowedValueRange.ordinal()] = 19;
                } catch (NoSuchFieldError e5) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.argument.ordinal()] = 9;
                } catch (NoSuchFieldError e6) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.argumentList.ordinal()] = 8;
                } catch (NoSuchFieldError e7) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.dataType.ordinal()] = 15;
                } catch (NoSuchFieldError e8) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.defaultValue.ordinal()] = 16;
                } catch (NoSuchFieldError e9) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.direction.ordinal()] = 10;
                } catch (NoSuchFieldError e10) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.major.ordinal()] = 3;
                } catch (NoSuchFieldError e11) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.maximum.ordinal()] = 21;
                } catch (NoSuchFieldError e12) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.minimum.ordinal()] = 20;
                } catch (NoSuchFieldError e13) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.minor.ordinal()] = 4;
                } catch (NoSuchFieldError e14) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.name.ordinal()] = 7;
                } catch (NoSuchFieldError e15) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.relatedStateVariable.ordinal()] = 11;
                } catch (NoSuchFieldError e16) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.retval.ordinal()] = 12;
                } catch (NoSuchFieldError e17) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.scpd.ordinal()] = 1;
                } catch (NoSuchFieldError e18) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.serviceStateTable.ordinal()] = 13;
                } catch (NoSuchFieldError e19) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.specVersion.ordinal()] = 2;
                } catch (NoSuchFieldError e20) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.stateVariable.ordinal()] = 14;
                } catch (NoSuchFieldError e21) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.step.ordinal()] = 22;
                } catch (NoSuchFieldError e22) {
                }
                $SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Service$ELEMENT = iArr;
            }
            return iArr;
        }

        public ActionHandler(MutableAction instance, ServiceDescriptorHandler parent) {
            super(instance, parent);
        }

        @Override // org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl.ServiceDescriptorHandler
        public void startElement(Descriptor.Service.ELEMENT element, Attributes attributes) throws SAXException {
            if (element.equals(ActionArgumentListHandler.EL)) {
                List<MutableActionArgument> arguments = new ArrayList<>();
                ((MutableAction) getInstance()).arguments = arguments;
                new ActionArgumentListHandler(arguments, this);
            }
        }

        @Override // org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl.ServiceDescriptorHandler
        public void endElement(Descriptor.Service.ELEMENT element) throws SAXException {
            switch ($SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Service$ELEMENT()[element.ordinal()]) {
                case 7:
                    ((MutableAction) getInstance()).name = getCharacters();
                    break;
            }
        }

        @Override // org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl.ServiceDescriptorHandler
        public boolean isLastElement(Descriptor.Service.ELEMENT element) {
            return element.equals(EL);
        }
    }

    protected static class ActionArgumentListHandler extends ServiceDescriptorHandler<List<MutableActionArgument>> {
        public static final Descriptor.Service.ELEMENT EL = Descriptor.Service.ELEMENT.argumentList;

        public ActionArgumentListHandler(List<MutableActionArgument> instance, ServiceDescriptorHandler parent) {
            super(instance, parent);
        }

        @Override // org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl.ServiceDescriptorHandler
        public void startElement(Descriptor.Service.ELEMENT element, Attributes attributes) throws SAXException {
            if (element.equals(ActionArgumentHandler.EL)) {
                MutableActionArgument argument = new MutableActionArgument();
                ((List) getInstance()).add(argument);
                new ActionArgumentHandler(argument, this);
            }
        }

        @Override // org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl.ServiceDescriptorHandler
        public boolean isLastElement(Descriptor.Service.ELEMENT element) {
            return element.equals(EL);
        }
    }

    protected static class ActionArgumentHandler extends ServiceDescriptorHandler<MutableActionArgument> {
        private static /* synthetic */ int[] $SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Service$ELEMENT;
        public static final Descriptor.Service.ELEMENT EL = Descriptor.Service.ELEMENT.argument;

        static /* synthetic */ int[] $SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Service$ELEMENT() {
            int[] iArr = $SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Service$ELEMENT;
            if (iArr == null) {
                iArr = new int[Descriptor.Service.ELEMENT.valuesCustom().length];
                try {
                    iArr[Descriptor.Service.ELEMENT.action.ordinal()] = 6;
                } catch (NoSuchFieldError e) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.actionList.ordinal()] = 5;
                } catch (NoSuchFieldError e2) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.allowedValue.ordinal()] = 18;
                } catch (NoSuchFieldError e3) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.allowedValueList.ordinal()] = 17;
                } catch (NoSuchFieldError e4) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.allowedValueRange.ordinal()] = 19;
                } catch (NoSuchFieldError e5) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.argument.ordinal()] = 9;
                } catch (NoSuchFieldError e6) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.argumentList.ordinal()] = 8;
                } catch (NoSuchFieldError e7) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.dataType.ordinal()] = 15;
                } catch (NoSuchFieldError e8) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.defaultValue.ordinal()] = 16;
                } catch (NoSuchFieldError e9) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.direction.ordinal()] = 10;
                } catch (NoSuchFieldError e10) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.major.ordinal()] = 3;
                } catch (NoSuchFieldError e11) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.maximum.ordinal()] = 21;
                } catch (NoSuchFieldError e12) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.minimum.ordinal()] = 20;
                } catch (NoSuchFieldError e13) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.minor.ordinal()] = 4;
                } catch (NoSuchFieldError e14) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.name.ordinal()] = 7;
                } catch (NoSuchFieldError e15) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.relatedStateVariable.ordinal()] = 11;
                } catch (NoSuchFieldError e16) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.retval.ordinal()] = 12;
                } catch (NoSuchFieldError e17) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.scpd.ordinal()] = 1;
                } catch (NoSuchFieldError e18) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.serviceStateTable.ordinal()] = 13;
                } catch (NoSuchFieldError e19) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.specVersion.ordinal()] = 2;
                } catch (NoSuchFieldError e20) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.stateVariable.ordinal()] = 14;
                } catch (NoSuchFieldError e21) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.step.ordinal()] = 22;
                } catch (NoSuchFieldError e22) {
                }
                $SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Service$ELEMENT = iArr;
            }
            return iArr;
        }

        public ActionArgumentHandler(MutableActionArgument instance, ServiceDescriptorHandler parent) {
            super(instance, parent);
        }

        @Override // org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl.ServiceDescriptorHandler
        public void endElement(Descriptor.Service.ELEMENT element) throws SAXException {
            switch ($SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Service$ELEMENT()[element.ordinal()]) {
                case 7:
                    ((MutableActionArgument) getInstance()).name = getCharacters();
                    break;
                case 10:
                    ((MutableActionArgument) getInstance()).direction = ActionArgument.Direction.valueOf(getCharacters().toUpperCase());
                    break;
                case 11:
                    ((MutableActionArgument) getInstance()).relatedStateVariable = getCharacters();
                    break;
                case 12:
                    ((MutableActionArgument) getInstance()).retval = true;
                    break;
            }
        }

        @Override // org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl.ServiceDescriptorHandler
        public boolean isLastElement(Descriptor.Service.ELEMENT element) {
            return element.equals(EL);
        }
    }

    protected static class StateVariableListHandler extends ServiceDescriptorHandler<List<MutableStateVariable>> {
        public static final Descriptor.Service.ELEMENT EL = Descriptor.Service.ELEMENT.serviceStateTable;

        public StateVariableListHandler(List<MutableStateVariable> instance, ServiceDescriptorHandler parent) {
            super(instance, parent);
        }

        @Override // org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl.ServiceDescriptorHandler
        public void startElement(Descriptor.Service.ELEMENT element, Attributes attributes) throws SAXException {
            if (element.equals(StateVariableHandler.EL)) {
                MutableStateVariable stateVariable = new MutableStateVariable();
                String sendEventsAttributeValue = attributes.getValue(Descriptor.Service.ATTRIBUTE.sendEvents.toString());
                stateVariable.eventDetails = new StateVariableEventDetails(sendEventsAttributeValue != null && sendEventsAttributeValue.toUpperCase().equals("YES"));
                ((List) getInstance()).add(stateVariable);
                new StateVariableHandler(stateVariable, this);
            }
        }

        @Override // org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl.ServiceDescriptorHandler
        public boolean isLastElement(Descriptor.Service.ELEMENT element) {
            return element.equals(EL);
        }
    }

    protected static class StateVariableHandler extends ServiceDescriptorHandler<MutableStateVariable> {
        private static /* synthetic */ int[] $SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Service$ELEMENT;
        public static final Descriptor.Service.ELEMENT EL = Descriptor.Service.ELEMENT.stateVariable;

        static /* synthetic */ int[] $SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Service$ELEMENT() {
            int[] iArr = $SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Service$ELEMENT;
            if (iArr == null) {
                iArr = new int[Descriptor.Service.ELEMENT.valuesCustom().length];
                try {
                    iArr[Descriptor.Service.ELEMENT.action.ordinal()] = 6;
                } catch (NoSuchFieldError e) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.actionList.ordinal()] = 5;
                } catch (NoSuchFieldError e2) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.allowedValue.ordinal()] = 18;
                } catch (NoSuchFieldError e3) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.allowedValueList.ordinal()] = 17;
                } catch (NoSuchFieldError e4) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.allowedValueRange.ordinal()] = 19;
                } catch (NoSuchFieldError e5) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.argument.ordinal()] = 9;
                } catch (NoSuchFieldError e6) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.argumentList.ordinal()] = 8;
                } catch (NoSuchFieldError e7) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.dataType.ordinal()] = 15;
                } catch (NoSuchFieldError e8) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.defaultValue.ordinal()] = 16;
                } catch (NoSuchFieldError e9) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.direction.ordinal()] = 10;
                } catch (NoSuchFieldError e10) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.major.ordinal()] = 3;
                } catch (NoSuchFieldError e11) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.maximum.ordinal()] = 21;
                } catch (NoSuchFieldError e12) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.minimum.ordinal()] = 20;
                } catch (NoSuchFieldError e13) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.minor.ordinal()] = 4;
                } catch (NoSuchFieldError e14) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.name.ordinal()] = 7;
                } catch (NoSuchFieldError e15) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.relatedStateVariable.ordinal()] = 11;
                } catch (NoSuchFieldError e16) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.retval.ordinal()] = 12;
                } catch (NoSuchFieldError e17) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.scpd.ordinal()] = 1;
                } catch (NoSuchFieldError e18) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.serviceStateTable.ordinal()] = 13;
                } catch (NoSuchFieldError e19) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.specVersion.ordinal()] = 2;
                } catch (NoSuchFieldError e20) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.stateVariable.ordinal()] = 14;
                } catch (NoSuchFieldError e21) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.step.ordinal()] = 22;
                } catch (NoSuchFieldError e22) {
                }
                $SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Service$ELEMENT = iArr;
            }
            return iArr;
        }

        public StateVariableHandler(MutableStateVariable instance, ServiceDescriptorHandler parent) {
            super(instance, parent);
        }

        @Override // org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl.ServiceDescriptorHandler
        public void startElement(Descriptor.Service.ELEMENT element, Attributes attributes) throws SAXException {
            if (element.equals(AllowedValueListHandler.EL)) {
                List<String> allowedValues = new ArrayList<>();
                ((MutableStateVariable) getInstance()).allowedValues = allowedValues;
                new AllowedValueListHandler(allowedValues, this);
            }
            if (element.equals(AllowedValueRangeHandler.EL)) {
                MutableAllowedValueRange allowedValueRange = new MutableAllowedValueRange();
                ((MutableStateVariable) getInstance()).allowedValueRange = allowedValueRange;
                new AllowedValueRangeHandler(allowedValueRange, this);
            }
        }

        @Override // org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl.ServiceDescriptorHandler
        public void endElement(Descriptor.Service.ELEMENT element) throws SAXException {
            switch ($SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Service$ELEMENT()[element.ordinal()]) {
                case 7:
                    ((MutableStateVariable) getInstance()).name = getCharacters();
                    break;
                case 15:
                    String dtName = getCharacters();
                    Datatype.Builtin builtin = Datatype.Builtin.getByDescriptorName(dtName);
                    ((MutableStateVariable) getInstance()).dataType = builtin != null ? builtin.getDatatype() : new CustomDatatype(dtName);
                    break;
                case 16:
                    ((MutableStateVariable) getInstance()).defaultValue = getCharacters();
                    break;
            }
        }

        @Override // org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl.ServiceDescriptorHandler
        public boolean isLastElement(Descriptor.Service.ELEMENT element) {
            return element.equals(EL);
        }
    }

    protected static class AllowedValueListHandler extends ServiceDescriptorHandler<List<String>> {
        private static /* synthetic */ int[] $SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Service$ELEMENT;
        public static final Descriptor.Service.ELEMENT EL = Descriptor.Service.ELEMENT.allowedValueList;

        static /* synthetic */ int[] $SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Service$ELEMENT() {
            int[] iArr = $SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Service$ELEMENT;
            if (iArr == null) {
                iArr = new int[Descriptor.Service.ELEMENT.valuesCustom().length];
                try {
                    iArr[Descriptor.Service.ELEMENT.action.ordinal()] = 6;
                } catch (NoSuchFieldError e) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.actionList.ordinal()] = 5;
                } catch (NoSuchFieldError e2) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.allowedValue.ordinal()] = 18;
                } catch (NoSuchFieldError e3) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.allowedValueList.ordinal()] = 17;
                } catch (NoSuchFieldError e4) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.allowedValueRange.ordinal()] = 19;
                } catch (NoSuchFieldError e5) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.argument.ordinal()] = 9;
                } catch (NoSuchFieldError e6) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.argumentList.ordinal()] = 8;
                } catch (NoSuchFieldError e7) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.dataType.ordinal()] = 15;
                } catch (NoSuchFieldError e8) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.defaultValue.ordinal()] = 16;
                } catch (NoSuchFieldError e9) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.direction.ordinal()] = 10;
                } catch (NoSuchFieldError e10) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.major.ordinal()] = 3;
                } catch (NoSuchFieldError e11) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.maximum.ordinal()] = 21;
                } catch (NoSuchFieldError e12) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.minimum.ordinal()] = 20;
                } catch (NoSuchFieldError e13) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.minor.ordinal()] = 4;
                } catch (NoSuchFieldError e14) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.name.ordinal()] = 7;
                } catch (NoSuchFieldError e15) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.relatedStateVariable.ordinal()] = 11;
                } catch (NoSuchFieldError e16) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.retval.ordinal()] = 12;
                } catch (NoSuchFieldError e17) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.scpd.ordinal()] = 1;
                } catch (NoSuchFieldError e18) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.serviceStateTable.ordinal()] = 13;
                } catch (NoSuchFieldError e19) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.specVersion.ordinal()] = 2;
                } catch (NoSuchFieldError e20) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.stateVariable.ordinal()] = 14;
                } catch (NoSuchFieldError e21) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.step.ordinal()] = 22;
                } catch (NoSuchFieldError e22) {
                }
                $SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Service$ELEMENT = iArr;
            }
            return iArr;
        }

        public AllowedValueListHandler(List<String> instance, ServiceDescriptorHandler parent) {
            super(instance, parent);
        }

        @Override // org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl.ServiceDescriptorHandler
        public void endElement(Descriptor.Service.ELEMENT element) throws SAXException {
            switch ($SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Service$ELEMENT()[element.ordinal()]) {
                case 18:
                    ((List) getInstance()).add(getCharacters());
                    break;
            }
        }

        @Override // org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl.ServiceDescriptorHandler
        public boolean isLastElement(Descriptor.Service.ELEMENT element) {
            return element.equals(EL);
        }
    }

    protected static class AllowedValueRangeHandler extends ServiceDescriptorHandler<MutableAllowedValueRange> {
        private static /* synthetic */ int[] $SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Service$ELEMENT;
        public static final Descriptor.Service.ELEMENT EL = Descriptor.Service.ELEMENT.allowedValueRange;

        static /* synthetic */ int[] $SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Service$ELEMENT() {
            int[] iArr = $SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Service$ELEMENT;
            if (iArr == null) {
                iArr = new int[Descriptor.Service.ELEMENT.valuesCustom().length];
                try {
                    iArr[Descriptor.Service.ELEMENT.action.ordinal()] = 6;
                } catch (NoSuchFieldError e) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.actionList.ordinal()] = 5;
                } catch (NoSuchFieldError e2) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.allowedValue.ordinal()] = 18;
                } catch (NoSuchFieldError e3) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.allowedValueList.ordinal()] = 17;
                } catch (NoSuchFieldError e4) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.allowedValueRange.ordinal()] = 19;
                } catch (NoSuchFieldError e5) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.argument.ordinal()] = 9;
                } catch (NoSuchFieldError e6) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.argumentList.ordinal()] = 8;
                } catch (NoSuchFieldError e7) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.dataType.ordinal()] = 15;
                } catch (NoSuchFieldError e8) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.defaultValue.ordinal()] = 16;
                } catch (NoSuchFieldError e9) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.direction.ordinal()] = 10;
                } catch (NoSuchFieldError e10) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.major.ordinal()] = 3;
                } catch (NoSuchFieldError e11) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.maximum.ordinal()] = 21;
                } catch (NoSuchFieldError e12) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.minimum.ordinal()] = 20;
                } catch (NoSuchFieldError e13) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.minor.ordinal()] = 4;
                } catch (NoSuchFieldError e14) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.name.ordinal()] = 7;
                } catch (NoSuchFieldError e15) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.relatedStateVariable.ordinal()] = 11;
                } catch (NoSuchFieldError e16) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.retval.ordinal()] = 12;
                } catch (NoSuchFieldError e17) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.scpd.ordinal()] = 1;
                } catch (NoSuchFieldError e18) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.serviceStateTable.ordinal()] = 13;
                } catch (NoSuchFieldError e19) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.specVersion.ordinal()] = 2;
                } catch (NoSuchFieldError e20) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.stateVariable.ordinal()] = 14;
                } catch (NoSuchFieldError e21) {
                }
                try {
                    iArr[Descriptor.Service.ELEMENT.step.ordinal()] = 22;
                } catch (NoSuchFieldError e22) {
                }
                $SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Service$ELEMENT = iArr;
            }
            return iArr;
        }

        public AllowedValueRangeHandler(MutableAllowedValueRange instance, ServiceDescriptorHandler parent) {
            super(instance, parent);
        }

        @Override // org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl.ServiceDescriptorHandler
        public void endElement(Descriptor.Service.ELEMENT element) throws SAXException {
            try {
                switch ($SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Service$ELEMENT()[element.ordinal()]) {
                    case 20:
                        ((MutableAllowedValueRange) getInstance()).minimum = Long.valueOf(getCharacters());
                        break;
                    case 21:
                        ((MutableAllowedValueRange) getInstance()).maximum = Long.valueOf(getCharacters());
                        break;
                    case 22:
                        ((MutableAllowedValueRange) getInstance()).step = Long.valueOf(getCharacters());
                        break;
                }
            } catch (Exception e) {
            }
        }

        @Override // org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl.ServiceDescriptorHandler
        public boolean isLastElement(Descriptor.Service.ELEMENT element) {
            return element.equals(EL);
        }
    }

    protected static class ServiceDescriptorHandler<I> extends SAXParser.Handler<I> {
        public ServiceDescriptorHandler(I instance) {
            super(instance);
        }

        public ServiceDescriptorHandler(I instance, SAXParser parser) {
            super(instance, parser);
        }

        public ServiceDescriptorHandler(I instance, ServiceDescriptorHandler parent) {
            super(instance, parent);
        }

        public ServiceDescriptorHandler(I instance, SAXParser parser, ServiceDescriptorHandler parent) {
            super(instance, parser, parent);
        }

        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            Descriptor.Service.ELEMENT el = Descriptor.Service.ELEMENT.valueOrNullOf(localName);
            if (el != null) {
                startElement(el, attributes);
            }
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            Descriptor.Service.ELEMENT el = Descriptor.Service.ELEMENT.valueOrNullOf(localName);
            if (el != null) {
                endElement(el);
            }
        }

        protected boolean isLastElement(String uri, String localName, String qName) {
            Descriptor.Service.ELEMENT el = Descriptor.Service.ELEMENT.valueOrNullOf(localName);
            return el != null && isLastElement(el);
        }

        public void startElement(Descriptor.Service.ELEMENT element, Attributes attributes) throws SAXException {
        }

        public void endElement(Descriptor.Service.ELEMENT element) throws SAXException {
        }

        public boolean isLastElement(Descriptor.Service.ELEMENT element) {
            return false;
        }
    }
}
